package com.example.matcha.controller;

import com.example.matcha.entity.Review;
import com.example.matcha.entity.Product;
import com.example.matcha.repository.ReviewRepository;
import com.example.matcha.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.Collections; // List.of()の代わりに利用

/**
 * レビュー投稿フォームや一覧などのビュー（HTML）を担当するコントローラーです。
 * Thymeleafテンプレートにデータを渡す役割を持ちます。
 */
@Controller
public class ReviewPageController {

    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ProductRepository productRepository;

    /**
     * レビュー投稿フォームを表示します。
     * 【修正点】500エラーの原因特定のため、try-catchを追加しました。
     */
    @GetMapping("/review/form")
    public String showReviewForm(Model model) {
        try {
            // 1. 商品一覧を取得 (ここでDB接続エラーが発生する可能性が高い)
            List<Product> allProducts = productRepository.findAll();
            
            // 2. Modelに追加
            model.addAttribute("allProducts", allProducts);
            model.addAttribute("review", new Review());
            
            System.out.println("DEBUG: showReviewForm のデータ取得に成功しました。");
            
            // 3. テンプレートを返す
            return "review_form";
            
        } catch (Exception e) {
            // 🚨 エラーが発生した場合、コンソールに詳細を出力する 🚨
            System.err.println("------------------------------------------------------------------");
            System.err.println("🚨 ReviewPageController#showReviewForm で重大なエラーが発生しました (500) 🚨");
            System.err.println("原因: " + e.getMessage());
            e.printStackTrace(); // スタックトレースを詳細に出力
            System.err.println("------------------------------------------------------------------");

            // エラーが発生しても、最低限のデータをModelに設定し、フォーム表示を試みます。
            // (ThymeleafでallProductsがnullの場合のエラーを防ぐため)
            model.addAttribute("allProducts", Collections.emptyList()); 
            model.addAttribute("review", new Review()); 
            model.addAttribute("errorMessage", "商品の取得中にサーバーエラーが発生しました。ログを確認してください。");
            
            return "review_form"; // エラーメッセージ付きでフォームを再表示
        }
    }
    
    /**
     * レビュー投稿処理を実行します。
     * URL: /review/form (POST)
     */
    @PostMapping("/review/form")
    public String postReview(
        @Valid @ModelAttribute("review") Review review, 
        BindingResult bindingResult, 
        RedirectAttributes redirectAttributes,
        Model model) {

        // バリデーションエラー処理
        if (bindingResult.hasErrors()) {
            // エラー時は再度商品一覧を取得してModelに設定し直す必要がある
            List<Product> allProducts = productRepository.findAll();
            model.addAttribute("allProducts", allProducts);
            return "review_form"; 
        }

        Optional<Product> productOpt = productRepository.findById(review.getProductId());

        if (productOpt.isEmpty()) {
            model.addAttribute("errorMessage", "指定された商品IDが見つかりませんでした。");
            List<Product> allProducts = productRepository.findAll();
            model.addAttribute("allProducts", allProducts);
            return "review_form";
        }
        
        review.setProduct(productOpt.get());
        
        try {
            reviewRepository.save(review);
            redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました！");
            return "redirect:/reviews/list"; // レビュー一覧ページにリダイレクト
            
        } catch (Exception e) {
            System.err.println("レビュー保存エラー: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "レビューの保存中に予期せぬエラーが発生しました。");
            return "redirect:/review/form";
        }
    }

    /**
     * 【POINT】レビュー一覧ページを表示します。
     * URL: /reviews/list (GET)
     */
    @GetMapping("/reviews/list")
    public String showReviewList(Model model) {
        // 1. データベースから全てのレビューを取得します。
        List<Review> reviews = reviewRepository.findAll();
        
        // 2. 各レビューに対して商品名を紐づける
        for (Review review : reviews) {
            // LazyInitializationException回避のため、ProductのIDを使って商品名を取得します。
            if (review.getProduct() != null) {
                Long productId = review.getProduct().getId();
                
                Optional<Product> productOpt = productRepository.findById(productId);
                
                if (productOpt.isPresent()) {
                    review.setProductName(productOpt.get().getName());
                } else {
                    review.setProductName("商品情報が見つかりません");
                }
            } else {
                 review.setProductName("商品情報が見つかりません (リレーションエラー)");
            }
        }
        
        model.addAttribute("reviews", reviews);
        
        // テンプレート名 ("review_list.html") を返します。
        return "review_list";
    }
}