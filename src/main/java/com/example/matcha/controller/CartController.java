package com.example.matcha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model; 
import org.springframework.web.bind.annotation.*;

import com.example.matcha.entity.CartItem;
import com.example.matcha.entity.Product; 
import com.example.matcha.repository.CartItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;

/**
 * カート機能（Thymeleaf表示とREST API）を管理するコントローラー
 */
@Controller 
public class CartController {

    @Autowired
    private CartItemRepository cartItemRepository;
    
    // ==========================================
    // 1. カート一覧表示（Thymeleaf View）の処理
    // ==========================================
    
    /**
     * カートの内容を取得し、商品名と数量を表示用に加工してThymeleafに渡します。
     * @param model Thymeleafに渡すデータモデル
     * @return カート一覧のビュー名 ("cart_list")
     */
    @GetMapping("/cart_list")
    public String showCart(Model model) {
        
        // データベースからCartItemを取得（@ManyToOneによりProductもフェッチされる）
        List<CartItem> cartItems = cartItemRepository.findAll();
        
        // 表示用のデータ構造（商品名、数量、CartItem ID）を作成
        List<Map<String, Object>> cartItemsForView = new ArrayList<>();
        
        for (CartItem item : cartItems) {
            
            // CartItem.getProduct().getName() を使用
            Product product = item.getProduct();
            String productName = (product != null) ? product.getName() : "不明な商品";
            
            // Thymeleafで利用するMapを作成
            cartItemsForView.add(Map.of(
                "id", item.getId(),        // CartItemのID (削除用)
                "productName", productName, // 商品名
                "quantity", item.getQuantity() // 数量
            ));
        }

        // Thymeleafの cart_list.html に表示用リストを渡す
        model.addAttribute("cartItems", cartItemsForView); 
        
        return "cart_list"; // cart_list.html を返す
    }
    
    
    // ==========================================
    // 2. API エンドポイント (非同期操作用)
    // ==========================================

    @PostMapping("/api/cart")
    public ResponseEntity<CartItem> addItem(@RequestBody CartItem item) {
        // カートに商品を追加
        CartItem savedItem = cartItemRepository.save(item);
        return ResponseEntity.ok(savedItem);
    }

    @GetMapping("/api/cart")
    public List<CartItem> getCartItems() {
        // カートの内容を取得
        return cartItemRepository.findAll();
    }

    @DeleteMapping("/api/cart/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId) {
        // カートから商品を削除
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