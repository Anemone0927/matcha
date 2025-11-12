package com.example.matcha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model; // Modelをインポート
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // RedirectAttributesをインポート

import com.example.matcha.entity.CartItem;
import com.example.matcha.repository.CartItemRepository;

import java.util.List;
import java.util.Optional;

/**
 * カート機能のAPI操作とView表示を管理するコントローラー
 */
@Controller 
public class CartController { // @RequestMapping("/api/cart") を削除し、ルーティングを個々のメソッドで管理

    @Autowired
    private CartItemRepository cartItemRepository;
    
    // ==========================================
    // 1. View表示エンドポイント
    // ==========================================

    /**
     * カート一覧ページを表示します (GET /cart_list)
     * cart_list.html からこのエンドポイントが呼ばれることを想定
     */
    @GetMapping("/cart_list")
    public String showCartList(Model model) {
        // Thymeleafで必要なデータをModelに追加
        List<CartItem> cartItems = cartItemRepository.findAll();
        model.addAttribute("cartItems", cartItems);
        
        // 合計金額の計算 (仮実装。実際はServiceで計算すべき)
        double totalPrice = cartItems.stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
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
    @PostMapping("/api/cart") // フルパスを記載
    @ResponseBody // JSONレスポンスを返すために必要
    public ResponseEntity<CartItem> addItem(@RequestBody CartItem item) {
        // カートに商品を追加
        CartItem savedItem = cartItemRepository.save(item);
        return ResponseEntity.ok(savedItem);
    }

    /**
     * カートの内容を全て取得します（GET /api/cart）
     */
    @GetMapping("/api/cart") // フルパスを記載
    @ResponseBody // JSONレスポンスを返すために必要
    public List<CartItem> getCartItems() {
        // カートの内容を取得
        return cartItemRepository.findAll();
    }

    /**
     * カートから商品を削除します（POST /api/cart/{itemId} でDELETEをシミュレート）
     * ここが最も重要な修正点です。
     * @ResponseBody を削除し、リダイレクトを返します。
     */
    @RequestMapping(value = "/api/cart/{itemId}", method = {RequestMethod.DELETE, RequestMethod.POST})
    // ↑ POSTとDELETEの両方を受け付けるように変更 (HiddenHttpMethodFilterがDELETEとして処理した後もPOSTとして認識されるため)
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
            // エラーログを出力してデバッグしやすくします
            System.err.println("カートアイテム削除中にエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "削除処理中にエラーが発生しました。");
            return "redirect:/cart_list"; 
        }
    }
}