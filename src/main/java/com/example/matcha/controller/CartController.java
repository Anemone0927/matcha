package com.example.matcha.controller;

import com.example.matcha.entity.CartItem;
import com.example.matcha.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// クラス全体を /cart にマッピング
@Controller
@RequestMapping("/cart")
public class CartController {

    // CartItemRepositoryの代わりにCartServiceを注入
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // ユーザー認証の仕組みがないため、ダミーのユーザーIDを使用します
    // 実際にはSpring SecurityなどからログインユーザーのIDを取得します
    private String getCurrentUserId() {
        // ThymeleafのURLは /cart/list ではなく /cart に統一
        return "user1"; // 元のControllerで使用されていたIDに合わせます
    }

    // --- 画面表示用エンドポイント ---

    /**
     * カート一覧画面を表示する (GET /cart)
     * CartServiceを利用してカートアイテムを取得します。
     * ThymeleafのURLをシンプルにするため、/listを削除し、/cartにマッピングします。
     */
    @GetMapping
    public String listCart(Model model) {
        String userId = getCurrentUserId();
        
        // CartServiceからカートアイテムを取得
        List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);
        model.addAttribute("cartItems", cartItems);
        
        // 合計金額はcart_list.html内でThymeleafの集計関数で計算するため、Java側での計算は不要です。
        
        return "cart_list"; // cart_list.html を返す
    }
    
    // --- カート追加エンドポイント (POST /cart/add) ---

    /**
     * カートに商品を追加する。
     * サービス層にロジックを委譲します。
     */
    @PostMapping("/add")
    public String addItem(@RequestParam("productId") Long productId, 
                          @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                          RedirectAttributes ra) {
        
        String userId = getCurrentUserId();
        
        try {
            // CartServiceを使って追加・更新ロジックを実行
            cartService.addItemToCart(userId, productId, quantity);
            ra.addFlashAttribute("successMessage", "商品をカートに追加しました！");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            // エラー時は適切なリダイレクト先（例えば商品詳細ページ）を設定すべきですが、今回はカート一覧に戻します
            return "redirect:/cart";
        }

        // リダイレクト先を /cart に修正 (listCartメソッドが /cart にあるため)
        return "redirect:/cart"; 
    }


    // --- 削除エンドポイント (POST /cart/{itemId}) ---
    
    /**
     * カートから商品を削除する (POST /cart/{itemId})
     * サービス層にロジックを委譲します。
     */
    @PostMapping("/{itemId}")
    public String deleteItemFromCart(@PathVariable("itemId") Long itemId, RedirectAttributes ra) {
        
        try {
            // CartServiceを使って削除ロジックを実行
            cartService.removeItemFromCart(itemId);
            ra.addFlashAttribute("successMessage", "カートアイテムを削除しました。");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "アイテムの削除に失敗しました。");
        }
        
        // リダイレクト先を /cart に修正
        return "redirect:/cart";
    }
}