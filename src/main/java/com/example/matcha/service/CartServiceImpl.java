package com.example.matcha.service;

import com.example.matcha.dto.CartItemDto;
import com.example.matcha.entity.CartItem;
import com.example.matcha.entity.Product;
import com.example.matcha.repository.CartItemRepository;
import com.example.matcha.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CartService インターフェースの実装クラス。
 */
@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartServiceImpl(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    /**
     * ユーザーIDに基づいてカートアイテムのリストを取得します。
     */
    @Override
    public List<CartItemDto> getCartItemsForCurrentUser(String userId) {
        List<CartItem> entities = cartItemRepository.findByUserId(userId);
        return entities.stream()
                .map(CartItemDto::fromEntity)
                .filter(dto -> dto != null) // 変換失敗（Productがnullなど）をフィルタリング
                .collect(Collectors.toList());
    }

    /**
     * カートに商品を追加、または数量を更新します。
     */
    @Override
    public CartItemDto addItemToCart(String userId, Long productId, int quantity) {
        
        // 1. 商品情報を取得し、見つからない場合は IllegalArgumentException をスロー
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("商品が見つかりません。Product ID: " + productId));

        // 2. 既存のカートアイテムを検索
        // 効率は悪いが、Repositoryにカスタムメソッドがない場合はこのロジックで動作する。
        Optional<CartItem> existingItemOpt = cartItemRepository.findByUserId(userId).stream()
                     .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(productId))
                     .findFirst();

        CartItem savedItem;

        if (existingItemOpt.isPresent()) {
            // 既存の商品があれば数量を更新
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            savedItem = cartItemRepository.save(existingItem);
        } else {
            // 新しいカートアイテムを作成
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            savedItem = cartItemRepository.save(newItem);
        }

        // 3. 保存された Entity を DTO に変換して返す
        return CartItemDto.fromEntity(savedItem);
    }

    /**
     * カートアイテムの数量を上書き更新します。
     */
    @Override
    public void updateQuantity(Long cartItemId, Integer newQuantity) {
        if (newQuantity <= 0) {
            removeItemFromCart(cartItemId);
            return;
        }

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("カートアイテムが見つかりません。CartItem ID: " + cartItemId));

        item.setQuantity(newQuantity);
        cartItemRepository.save(item);
    }

    /**
     * カートアイテムを削除します。
     */
    @Override
    public void removeItemFromCart(Long cartItemId) {
        if (cartItemRepository.existsById(cartItemId)) {
            cartItemRepository.deleteById(cartItemId);
        }
    }
}