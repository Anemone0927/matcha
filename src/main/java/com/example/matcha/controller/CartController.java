package com.example.matcha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.*; 

import com.example.matcha.entity.CartItem;
import com.example.matcha.repository.CartItemRepository;

import java.util.List;
import java.util.Optional;

/**
 * カート機能のうち、主に非同期操作（REST API）を管理するコントローラー
 * ThymeleafのView表示機能（/cart_list）は、競合解消のため削除しました。
 */
@Controller 
@RequestMapping("/api/cart") // 全てのエンドポイントのベースを /api/cart に設定
public class CartController {

    @Autowired
    private CartItemRepository cartItemRepository;
    
    
    // ==========================================
    // 1. API エンドポイント (非同期操作用)
    // ==========================================

    /**
     * カートに商品を追加します（POST /api/cart）
     */
    @PostMapping
    @ResponseBody // JSONレスポンスを返すために必要
    public ResponseEntity<CartItem> addItem(@RequestBody CartItem item) {
        // カートに商品を追加
        CartItem savedItem = cartItemRepository.save(item);
        return ResponseEntity.ok(savedItem);
    }

    /**
     * カートの内容を全て取得します（GET /api/cart）
     */
    @GetMapping
    @ResponseBody // JSONレスポンスを返すために必要
    public List<CartItem> getCartItems() {
        // カートの内容を取得
        return cartItemRepository.findAll();
    }

    /**
     * カートから商品を削除します（DELETE /api/cart/{itemId}）
     */
    @DeleteMapping("/{itemId}")
    @ResponseBody // JSONレスポンスを返すために必要
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