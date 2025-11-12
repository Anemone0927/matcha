package com.example.matcha.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; 
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 

import com.example.matcha.entity.CartItem;
import com.example.matcha.repository.CartItemRepository;

import java.util.List;
import java.util.Optional;

/**
 * カート機能のAPI操作とView表示を管理するコントローラー
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
    // 1. View表示エンドポイント
    // ==========================================

    /**
     * カート一覧ページを表示します (GET /cart_list)
     */
    @GetMapping("/cart_list")
    public String showCartList(Model model) {
        // 現状はユーザーIDでの絞り込みがないため findAll() を使用
        List<CartItem> cartItems = cartItemRepository.findAll();
        model.addAttribute("cartItems", cartItems);
        
        // 合計金額の計算を product.getPrice() を使用するように修正
        double totalPrice = cartItems.stream()
            .mapToDouble(item -> {
                // 商品が関連付けられていない場合のNullPointerExceptionを避けるガード処理
                if (item.getProduct() == null) {
                    logger.warn("CartItem ID: {} に関連付けられた商品がありません。", item.getId());
                    return 0.0;
                }
                // 商品の価格 * 数量 で合計を計算
                return (double) item.getProduct().getPrice() * item.getQuantity();
            })
            .sum();
            
        model.addAttribute("totalPrice", totalPrice);
        
        return "cart_list"; // cart_list.html をレンダリング
    }

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
            // 処理後、カート一覧ページにリダイレクト
            return "redirect:/cart_list";
        } catch (Exception e) {
            // エラーログを出力
            logger.error("カートアイテム削除中にエラーが発生しました。", e);
            redirectAttributes.addFlashAttribute("error", "削除処理中にエラーが発生しました。");
            return "redirect:/cart_list";
        }
    }
}