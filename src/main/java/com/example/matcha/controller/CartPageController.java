package com.example.matcha.controller;

import com.example.matcha.entity.Product;
import com.example.matcha.entity.CartItem; // 追加
import com.example.matcha.repository.ProductRepository;
import com.example.matcha.repository.CartItemRepository; // 追加
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Controller
public class CartPageController {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository; // カートデータを取得するために必要

    // 💡 コンストラクタで2つのRepositoryを受け取ります
    public CartPageController(ProductRepository productRepository, CartItemRepository cartItemRepository) {
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository; // 初期化
    }

    // カート追加フォーム
    @GetMapping("/cart/add")
    public String showAddCartForm(Model model) {
        
        // データベースから全商品リストを取得
        List<Product> allProducts = productRepository.findAll();
        
        // モデルに商品リストを追加 (HTMLで使うため)
        model.addAttribute("allProducts", allProducts);
        
        return "add_cart"; // add_cart.html を表示する
    }
    
    // カート一覧表示（修正ロジック）
    @GetMapping("/cart_list") 
    public String showCartListForm(Model model) {
        
        // 1. データベースからCartItemを取得
        List<CartItem> cartItems = cartItemRepository.findAll();
        
        // 2. 表示用のデータ構造（商品名、数量、CartItem ID、価格など）を作成
        List<Map<String, Object>> cartItemsForView = new ArrayList<>();
        
        for (CartItem item : cartItems) {
            
            Product product = item.getProduct();
            String productName = (product != null) ? product.getName() : "不明な商品";
            Long productPrice = (product != null) ? product.getPrice() : 0L;
            
            // Thymeleafで利用するMapを作成
            cartItemsForView.add(Map.of(
                "id", item.getId(),        // CartItemのID (削除用)
                "productName", productName, // 商品名
                "quantity", item.getQuantity(), // 数量
                "price", productPrice,     // 単価
                "subtotal", productPrice * item.getQuantity() // 小計
            ));
        }

        // 3. Thymeleafの cart_list.html に表示用リストを渡す
        model.addAttribute("cartItems", cartItemsForView); 
        
        return "cart_list"; // cart_list.html を返す
    }
}