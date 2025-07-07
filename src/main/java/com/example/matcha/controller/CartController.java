package com.example.matcha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;  // 追加（Modelを使うため）
import org.springframework.ui.Model;         // 追加
import org.springframework.web.bind.annotation.*;

import com.example.matcha.entity.CartItem;
import com.example.matcha.repository.CartItemRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartItemRepository cartItemRepository;

    // カートに商品を追加
    @PostMapping
    public ResponseEntity<CartItem> addItem(@RequestBody CartItem item) {
        CartItem savedItem = cartItemRepository.save(item);
        return ResponseEntity.ok(savedItem);
    }

    // カートの内容を取得
    @GetMapping
    public List<CartItem> getCartItems() {
        return cartItemRepository.findAll();
    }

    // カートから商品を削除
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId) {
        Optional<CartItem> item = cartItemRepository.findById(itemId);
        if (item.isPresent()) {
            cartItemRepository.deleteById(itemId);
            return ResponseEntity.ok("削除しました");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

