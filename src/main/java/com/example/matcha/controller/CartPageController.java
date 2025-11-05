package com.example.matcha.controller;

// 💡 必要なインポートを追加します
import com.example.matcha.entity.Product;
import com.example.matcha.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
// ---

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Modelをインポート
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartPageController {

    // 💡 追記: ProductRepositoryをインジェクション（DI）します
    @Autowired
    private ProductRepository productRepository;

    // カート追加フォーム
    @GetMapping("/cart/add")
    public String showAddCartForm(Model model) { // 💡 追記: (Model model)
        
        // 💡 追記: データベースから全商品リストを取得
        List<Product> allProducts = productRepository.findAll();
        
        // 💡 追記: モデルに商品リストを追加 (HTMLで使うため)
        model.addAttribute("allProducts", allProducts);
        
        return "add_cart";  // add_cart.html を表示する
    }
    
    // カート一覧表示（パスを /cart_list に変更）
    @GetMapping("/cart_list") 
    public String showCartListForm(Model model) {
        // (ここは変更なし)
        model.addAttribute("cartItems", java.util.Collections.emptyList());
        return "cart_list"; 
    }
}