package com.example.matcha.service;

import com.example.matcha.entity.CartItem;
import com.example.matcha.entity.Product;
import com.example.matcha.model.CartItemModel; // DTOをインポート
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
 * データベース操作を行い、結果を DTO (CartItemModel) に変換して Controller に返します。
 */
@Service
@Transactional
public class CartServiceImpl implements CartService { // インターフェースを実装するように変更

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository; 

    @Autowired
    public CartServiceImpl(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    /**
     * ユーザーIDに基づいてカートアイテムのリストを取得します。
     * Entityを取得後、DTOに変換して返します。
     * @param userId 現在ログインしているユーザーのID
     * @return カートアイテムDTOのリスト
     */
    @Override // インターフェースメソッドの実装
    public List<CartItemModel> getCartItemsByUserId(String userId) {
        // 1. Entityリストを取得
        List<CartItem> entities = cartItemRepository.findByUserId(userId);
        
        // 2. EntityをDTOに変換して返す
        return entities.stream()
                .map(CartItemModel::fromEntity) // CartItemModel.fromEntity() を使用して変換
                .collect(Collectors.toList());
    }

    /**
     * カートに商品を追加、または数量を更新します。
     * @param userId 現在ログインしているユーザーのID
     * @param productId 追加する商品のID
     * @param quantity 追加する数量
     * @return 更新されたカートアイテムDTO
     */
    @Override // インターフェースメソッドの実装
    public CartItemModel addItemToCart(String userId, Long productId, int quantity) {
        // 1. 既存のカートアイテムを検索 (DBのカスタムメソッド利用を推奨しますが、今回は現状ロジックを維持)
        List<CartItem> existingItems = cartItemRepository.findByUserId(userId);
        Optional<CartItem> existingItemOpt = existingItems.stream()
                .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(productId))
                .findFirst();

        // 2. 商品情報を取得
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

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
        return CartItemModel.fromEntity(savedItem);
    }

    /**
     * カートアイテムを削除します。
     * @param cartItemId 削除対象のカートアイテムID
     */
    @Override // インターフェースメソッドの実装
    public void removeItemFromCart(Long cartItemId) {
        // ご提示のロジックをそのまま使用
        cartItemRepository.deleteById(cartItemId);
    }
}