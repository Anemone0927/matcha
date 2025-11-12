package com.example.matcha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; // 💡 @Controllerに戻す
import org.springframework.ui.Model; 
import org.springframework.web.bind.annotation.*;

import com.example.matcha.entity.CartItem;
import com.example.matcha.entity.Product; // 💡 Product エンティティのインポート
import com.example.matcha.repository.CartItemRepository;
import com.example.matcha.repository.ProductRepository; // 💡 ProductRepositoryのインポート

import java.util.ArrayList; // 💡 補助リスト用
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors; // 💡 ストリーム処理用

// @RestController を削除し、@Controllerのみ残します
@Controller 
public class CartController {

    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository; // 💡 ProductRepositoryを注入

    // ==========================================
    // 1. カート一覧表示（Thymeleaf View）の処理を追加
    // ==========================================
    
    // Thymeleafテンプレートを返すためのエンドポイント
    @GetMapping("/cart_list")
    public String showCart(Model model) {
        
        List<CartItem> cartItems = cartItemRepository.findAll();
        
        // 1. カート内の全ての Product ID を抽出
        List<Long> productIds = cartItems.stream()
            .map(CartItem::getProductId)
            .collect(Collectors.toList());
            
        // 2. 該当する全ての商品を一度にデータベースから取得 (N+1問題の回避)
        List<Product> products = productRepository.findAllById(productIds);
        
        // 3. 商品IDをキーとするMapを作成 (高速検索用)
        Map<Long, String> productNames = products.stream()
            .collect(Collectors.toMap(Product::getId, Product::getName));
            
        // 4. 表示用のデータ構造を作成
        List<Map<String, Object>> cartItemsWithNames = new ArrayList<>();
        
        for (CartItem item : cartItems) {
            String productName = productNames.getOrDefault(item.getProductId(), "不明な商品");
            
            // 💡 商品名、数量、CartItem IDを保持するMapを作成
            cartItemsWithNames.add(Map.of(
                "id", item.getId(),
                "productName", productName,
                "quantity", item.getQuantity()
            ));
        }

        // 💡 Thymeleafの cart_list.html に表示用リストを渡す
        model.addAttribute("cartItems", cartItemsWithNames); 
        
        return "cart_list"; // cart_list.html を返す
    }
    
    
    // ==========================================
    // 2. API エンドポイント
    // ==========================================

    @PostMapping("/api/cart")
    public ResponseEntity<CartItem> addItem(@RequestBody CartItem item) {
        // TODO: 既存商品がある場合は数量をインクリメントするロジックを追加すべき
        CartItem savedItem = cartItemRepository.save(item);
        return ResponseEntity.ok(savedItem);
    }

    @GetMapping("/api/cart")
    public List<CartItem> getCartItems() {
        return cartItemRepository.findAll();
    }

    @DeleteMapping("/api/cart/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId) {
        Optional<CartItem> item = cartItemRepository.findById(itemId);
        if (item.isPresent()) {
            cartItemRepository.deleteById(itemId);
            // 削除成功後、JSON APIとしてOKを返す
            return ResponseEntity.ok().body("{\"message\": \"削除しました\"}"); 
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}