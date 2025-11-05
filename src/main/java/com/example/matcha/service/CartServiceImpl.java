package com.example.matcha.service;

import com.example.matcha.entity.CartItem;
import com.example.matcha.entity.Product;
import com.example.matcha.model.CartItemModel;
import com.example.matcha.repository.CartItemRepository;
import com.example.matcha.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CartServiceインターフェースのJPA実装。
 * 永続化ロジックとEntity-to-DTO変換を担当します。
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
     * EntityをDTOに変換する内部ヘルパーメソッド。
     */
    private CartItemModel toModel(CartItem entity) {
        // CartItemのProductフィールドから情報を取得
        Product product = entity.getProduct();
        
        // 外部キーが解決できない場合や商品情報がない場合の安全策
        String productName = product != null ? product.getName() : "不明な商品";
        int productPrice = product != null ? product.getPrice() : 0;

        return new CartItemModel(
            entity.getId(),
            productName,
            entity.getQuantity(),
            productPrice
        );
    }

    /**
     * 現在のユーザーのカートアイテムをすべて取得し、DTOとして返します。
     * (CartControllerが期待する findAllItems の実装)
     */
    @Override
    @Transactional(readOnly = true)
    public List<CartItemModel> findAllItems() {
        // 💡 ユーザー認証が未実装のため、デモ用として仮のユーザーIDを使用します。
        // 本来はセキュリティコンテキストから取得します。
        String tempUserId = "current_user_id"; 
        
        List<CartItem> cartItems = cartItemRepository.findByUserId(tempUserId);
        
        // Entity Listを DTO Listに変換してControllerに返します
        return cartItems.stream()
                   .map(this::toModel)
                   .collect(Collectors.toList());
    }

    /**
     * 指定されたIDのカートアイテムを削除します。
     * (CartControllerが期待する deleteItem の実装)
     */
    @Override
    public void deleteItem(Long itemId) {
        cartItemRepository.deleteById(itemId);
    }

    /**
     * カートに商品を追加、または数量を更新します。
     */
    @Override
    public void addItemToCart(String userId, Long productId, int quantity) {
        List<CartItem> existingItems = cartItemRepository.findByUserId(userId);
        Optional<CartItem> existingItemOpt = existingItems.stream()
            .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(productId))
            .findFirst();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }
    }
}