package com.example.matcha.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.matcha.entity.Product;
import com.example.matcha.service.FavoriteService;
import com.example.matcha.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/favorite")
public class FavoriteController {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);
    private static final String SESSION_USER_ID_KEY = "loggedInUserId";
    private static final Long DEFAULT_USER_ID = 1L;

    private final FavoriteService favoriteService;
    private final ProductService productService;

    public FavoriteController(FavoriteService favoriteService, ProductService productService) {
        this.favoriteService = favoriteService;
        this.productService = productService;
    }

    /**
     * ログインしているユーザーのIDをセッションから取得
     */
    private Long getLoggedInUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID_KEY);
        return userId != null ? userId : DEFAULT_USER_ID; 
    }

    /**
     * マイページのお気に入りリスト画面を表示 (GET /favorite)
     */
    @GetMapping
    public String showFavoriteList(Model model, HttpSession session) {
        Long userId = getLoggedInUserId(session);
        logger.info("ユーザーID: {} のお気に入りリストを表示します。", userId);

        // 1. お気に入り商品IDのリストを取得
        List<Long> favoriteProductIds = favoriteService.getFavoriteProductIds(userId);

        // 2. 商品IDリストから、Productエンティティのリストを取得
        List<Product> favoriteProducts = productService.findProductsByIds(favoriteProductIds);

        // 3. お気に入りリストをModelに追加 (HTMLの th:each="product : ${favoriteProducts}" と対応)
        model.addAttribute("favoriteProducts", favoriteProducts);
        
        return "favorite"; 
    }

    /**
     * お気に入り解除処理 (POST /favorite/remove/{productId})
     * 処理完了後、お気に入り一覧画面へリダイレクトします。
     */
    @PostMapping("/remove/{productId}")
    public String removeFavorite(@PathVariable Long productId, HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = getLoggedInUserId(session);
        
        favoriteService.removeFavorite(userId, productId);
        logger.info("ユーザーID: {} が商品ID: {} のお気に入り登録を解除しました。", userId, productId);
        
        // 画面に一時的なメッセージを表示したい場合に使用
        redirectAttributes.addFlashAttribute("message", "お気に入りから削除しました。");
        
        return "redirect:/favorite"; 
    }

    /**
     * お気に入り登録処理 (POST /favorite/add/{productId})
     * 商品詳細ページなどから呼ばれることを想定。
     */
    @PostMapping("/add/{productId}")
    public String addFavorite(@PathVariable Long productId, HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = getLoggedInUserId(session);
        
        if (favoriteService.addFavorite(userId, productId)) {
            logger.info("ユーザーID: {} が商品ID: {} を登録。", userId, productId);
            redirectAttributes.addFlashAttribute("message", "お気に入りに追加しました！");
        } else {
            redirectAttributes.addFlashAttribute("message", "既にお気に入り登録されています。");
        }
        
        // 登録後、元の画面（お気に入り一覧）に戻す
        return "redirect:/favorite";
    }
}