package com.example.matcha.service;

import com.example.matcha.entity.CartItem;
import com.example.matcha.entity.Product;
import com.example.matcha.repository.CartItemRepository;
import com.example.matcha.repository.ProductRepository; // 💡 修正: ProductRepositoryのインポートを追加
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository; // 商品情報を取得するためにリポジトリが必要

    @Autowired
    // 💡 修正: ProductRepositoryをコンストラクタに追加
    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    /**
     * ユーザーIDに基づいてカートアイテムのリストを取得します。
     * @param userId 現在ログインしているユーザーのID
     * @return カートアイテムのリスト
     */
    public List<CartItem> getCartItemsByUserId(String userId) {
        // CartItemRepositoryで定義したfindByUserIdを利用
        return cartItemRepository.findByUserId(userId);
    }

    /**
     * カートに商品を追加、または数量を更新します。
     * ユーザーが既に同じ商品をカートに入れている場合、数量をインクリメントします。
     * @param userId 現在ログインしているユーザーのID
     * @param productId 追加する商品のID
     * @param quantity 追加する数量
     * @return 更新されたカートアイテム
     */
    public CartItem addItemToCart(String userId, Long productId, int quantity) {
        // 1. 既存のカートアイテムを検索
        // 実際には、userIdとproductIdの両方で検索するカスタムメソッドが望ましいですが、
        // 今回はシンプルに、まず全アイテムを取得し、該当商品をJava側で探します。
        
        List<CartItem> existingItems = cartItemRepository.findByUserId(userId);
        Optional<CartItem> existingItemOpt = existingItems.stream()
            .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(productId))
            .findFirst();

        // 2. 数量と商品情報を取得
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        if (existingItemOpt.isPresent()) {
            // 既存の商品があれば数量を更新
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartItemRepository.save(existingItem);
        } else {
            // 新しいカートアイテムを作成
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            return cartItemRepository.save(newItem);
        }
    }

    /**
     * カートアイテムを削除します。
     * @param cartItemId 削除対象のカートアイテムID
     */
    public void removeItemFromCart(Long cartItemId) {
        // 削除するだけで、ユーザーIDによる権限チェックはController側で行うのが理想
        cartItemRepository.deleteById(cartItemId);
    }
}