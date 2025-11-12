package com.example.matcha.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 

import com.example.matcha.entity.CartItem;
import com.example.matcha.repository.CartItemRepository;

import java.util.List;
import java.util.Optional;

/**
 * カート機能のAPI操作を管理するコントローラー (View表示機能は削除)
 */
@Controller
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final CartItemRepository cartItemRepository;
    
    // コンストラクタインジェクション
    public CartController(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }
    
    // ==========================================
    // 1. View表示エンドポイント (削除済み)
    //    -> CartPageController など、他のView専用コントローラで処理してください。
    // ==========================================

    // ==========================================
    // 2. API エンドポイント (非同期操作用)
    // ==========================================

    /**
     * カートに商品を追加します（POST /api/cart）
     */
    @PostMapping("/api/cart") 
    @ResponseBody 
    public ResponseEntity<CartItem> addItem(@RequestBody CartItem item) {
        // カートに商品を追加
        CartItem savedItem = cartItemRepository.save(item);
        return ResponseEntity.ok(savedItem);
    }

    /**
     * カートの内容を全て取得します（GET /api/cart）
     */
    @GetMapping("/api/cart") 
    @ResponseBody 
    public List<CartItem> getCartItems() {
        // カートの内容を取得
        return cartItemRepository.findAll();
    }

    /**
     * カートから商品を削除します（POST /api/cart/{itemId} でDELETEをシミュレート）
     * * 注: このエンドポイントは、APIとしてはDELETEを使用すべきですが、
     * ここではThymeleafからのリダイレクトを処理するためにPOST/DELETEを許可し、
     * View表示コントローラにリダイレクトしています。
     */
    @RequestMapping(value = "/api/cart/{itemId}", method = {RequestMethod.DELETE, RequestMethod.POST})
    public String deleteItem(@PathVariable Long itemId, RedirectAttributes redirectAttributes) {
        try {
            Optional<CartItem> item = cartItemRepository.findById(itemId);
            if (item.isPresent()) {
                cartItemRepository.deleteById(itemId);
                redirectAttributes.addFlashAttribute("message", "商品をカートから削除しました。");
            } else {
                redirectAttributes.addFlashAttribute("error", "指定された商品が見つかりませんでした。");
            }
            // 処理後、View表示用のURLにリダイレクト
            return "redirect:/cart_list";
        } catch (Exception e) {
            logger.error("カートアイテム削除中にエラーが発生しました。", e);
            redirectAttributes.addFlashAttribute("error", "削除処理中にエラーが発生しました。");
            return "redirect:/cart_list";
        }
    }
}