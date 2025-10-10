package com.example.matcha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Modelをインポート
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartPageController {

    // カート追加フォーム
    @GetMapping("/cart/add")
    public String showAddCartForm() {
        return "add_cart";  // add_cart.html を表示する
    }
    
    // カート一覧表示（パスを /cart_list に変更）
    @GetMapping("/cart_list") 
    public String showCartListForm(Model model) {
        // Thymeleafで利用する空のリストを渡す（現状は動作確認のため）
        model.addAttribute("cartItems", java.util.Collections.emptyList());
        return "cart_list";  // cart_list.html を表示する
    }

}
