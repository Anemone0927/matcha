package com.example.matcha.controller;

import com.example.matcha.model.CartItem;
import com.example.matcha.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * カートのページ表示と操作（削除）を担当するコントローラ。
 * ベースパスは /cart
 */
@Controller
@RequestMapping("/cart")
public class CartPageController {

    private final CartService cartService;

    // CartServiceの依存性注入 (コンストラクタインジェクション)
    public CartPageController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * カート一覧画面を表示します。
     * Thymeleafテンプレート "cart_list" を返します。
     */
    @GetMapping
    public String showCart(Model model) {
        // 現在のカートアイテムを取得し、Modelに追加
        // （認証がないため、ここではすべてのカートアイテムを取得する想定）
        List<CartItem> cartItems = cartService.findAllItems();
        model.addAttribute("cartItems", cartItems);
        
        // リダイレクト時にFlash Attributeとして追加されたメッセージ（成功/エラー）は
        // Modelに自動的に追加されているため、ここでは特別な処理は不要です。
        
        return "cart_list";
    }

    /**
     * カートアイテムを削除します。
     * cart_list.htmlのフォームからのPOSTリクエストを受け付けます。
     * @param itemId 削除対象のカートアイテムID
     */
    @PostMapping("/{itemId}")
    public String deleteCartItem(@PathVariable Long itemId, RedirectAttributes redirectAttributes) {
        try {
            cartService.deleteItem(itemId);
            redirectAttributes.addFlashAttribute("successMessage", "カートから商品を削除しました！");
        } catch (Exception e) {
            // エラーログを出力し、エラーメッセージをリダイレクト先に渡す
            System.err.println("カートアイテム削除中にエラーが発生しました: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "商品の削除中にエラーが発生しました。システム管理者に連絡してください。");
        }
        // 処理後、カート一覧画面にリダイレクト
        return "redirect:/cart";
    }

    /**
     * 商品追加ボタン（/cart/add）が押された際に、商品一覧ページにリダイレクトします。
     */
    @GetMapping("/add")
    public String redirectToProductList() {
        return "redirect:/products_list";
    }
}