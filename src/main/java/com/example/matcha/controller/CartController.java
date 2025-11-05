package com.example.matcha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // @Controllerに変更
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.example.matcha.entity.CartItem;
import com.example.matcha.repository.CartItemRepository;

import java.util.List;
import java.util.Optional;

// クラス全体を /cart にマッピング
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartItemRepository cartItemRepository;

    // --- 画面表示用エンドポイント ---

    /**
     * カート一覧画面を表示する (GET /cart/list)
     * カートアイテムと合計金額をモデルに格納する
     */
    @GetMapping("/list")
    public String listCart(Model model) {
        // 🚨 ユーザー認証が未実装のため、一旦ユーザーIDを固定
        String userId = "user1"; 

        // カートアイテムを取得
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        model.addAttribute("cartItems", cartItems);

        // 合計金額をJava側で計算
        int totalPrice = 0;
        for (CartItem item : cartItems) {
            // NullPointerExceptionを避けるためのチェック
            if (item.getProduct() != null && item.getProduct().getPrice() != null) {
                totalPrice += item.getProduct().getPrice() * item.getQuantity();
            }
        }
        
        // 合計金額をモデルに追加
        model.addAttribute("totalPrice", totalPrice);
        
        return "cart_list"; // cart_list.html を返す
    }
    
    // --- カート追加APIエンドポイント (POST /cart/add) ---

    /**
     * カートに商品を追加する。
     * フォームから送信された商品IDと数量を受け取る。（APIとして機能）
     * 成功後、カート一覧画面にリダイレクトする。
     */
    // ThymeleafフォームからのPOSTを受け取るためのエンドポイント
    @PostMapping("/add")
    public String addItem(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity) {
        // 🚨 ユーザー認証が未実装のため、一旦ユーザーIDを固定
        String userId = "user1";
        
        // TODO: ここに既存のアイテムを更新するロジックや、ProductRepositoryから商品を取得するロジックを実装する必要があります。
        // 現状のCartItemエンティティの構造（Product, Quantity, UserID）を考慮した上で、
        // 簡略化のため、ここでは仮のCartItemを作成して保存します。
        
        // 簡略化された保存処理（実際には商品IDを使って商品情報を取得・設定すべき）
        CartItem newItem = new CartItem();
        newItem.setUserId(userId);
        // 商品情報の設定（Productエンティティを別途取得して設定する必要がある）
        // newItem.setProduct(productRepository.findById(productId).orElse(null));
        newItem.setQuantity(quantity);

        // 一旦、簡略化された保存処理を実行（デバッグ目的）
        // cartItemRepository.save(newItem);
        
        return "redirect:/cart/list"; // カート一覧にリダイレクト
    }


    // --- 削除APIエンドポイント (POST /cart/{itemId}) ---
    
    /**
     * カートから商品を削除する (POST /cart/{itemId} + _method=delete)
     * Thymeleafフォームから送信されるため、Stringを返す。
     */
    @PostMapping("/{itemId}")
    public String deleteItemFromCart(@PathVariable Long itemId) {
        Optional<CartItem> item = cartItemRepository.findById(itemId);
        if (item.isPresent()) {
            cartItemRepository.deleteById(itemId);
            // 削除成功メッセージを一度セッションに追加してからリダイレクトするのが理想的ですが、
            // 簡略化のため、一覧画面にリダイレクトします。
        }
        
        return "redirect:/cart/list"; // 削除後、カート一覧にリダイレクト
    }

    // --- (注) 以前の `@RestController` のメソッドは、画面表示との兼ね合いで上記に統合・変更しました。 ---

}