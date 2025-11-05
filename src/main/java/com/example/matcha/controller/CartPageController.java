package com.example.matcha.controller;



// 必要なインポート

import com.example.matcha.entity.Product;

import com.example.matcha.repository.ProductRepository;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;



@Controller

public class CartPageController {



    // 💡 変更点1: @Autowired を削除し、final宣言にします

    // (これにより、変数がnullのままになるのを防ぎます)

    private final ProductRepository productRepository;



    // 💡 変更点2: コンストラクタで ProductRepository を受け取ります

    // (これがSpringで推奨される安全な方法です)

    public CartPageController(ProductRepository productRepository) {

        this.productRepository = productRepository;

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

    

    // カート一覧表示

    @GetMapping("/cart_list") 

    public String showCartListForm(Model model) {

        // (ここは変更なし)

        model.addAttribute("cartItems", java.util.Collections.emptyList());

        return "cart_list"; 

    }

}