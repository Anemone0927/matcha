package com.example.matcha.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;

import com.example.matcha.entity.Product;
import com.example.matcha.service.ProductService;
import com.example.matcha.service.FavoriteService;

@Controller
@RequestMapping
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private static final String SESSION_USER_ID_KEY = "loggedInUserId";

    private final ProductService productService;
    private final FavoriteService favoriteService;

    public ProductController(ProductService productService, FavoriteService favoriteService) {
        this.productService = productService;
        this.favoriteService = favoriteService;
    }

    private Long getLoggedInUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID_KEY);
        return (userId != null) ? userId : 1L; // デバッグ用仮ID
    }

    /**
     * 商品検索API (JSON)
     * 検索、フィルタ、ソートの結果にお気に入り情報を付与して返します。
     */
    @GetMapping("/api/products")
    @ResponseBody
    public List<Product> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sort,
            HttpSession session) {
        
        logger.info("API検索リクエスト: keyword={}, category={}, sort={}", keyword, category, sort);

        // 1. サービスで検索実行
        List<Product> products = productService.searchProducts(keyword, category, sort);

        // 2. お気に入り情報を付与
        Long userId = getLoggedInUserId(session);
        List<Long> favoriteIds = favoriteService.getFavoriteProductIds(userId);
        
        for (Product p : products) {
            p.setFavorited(favoriteIds.contains(p.getId()));
        }

        return products;
    }

    /**
     * 商品一覧画面 (Thymeleaf)
     */
    @GetMapping("/products_list")
    public String showList(Model model, HttpSession session) {
        Long userId = getLoggedInUserId(session);
        List<Product> products = productService.findAllProducts();
        
        // お気に入りIDのSetを作成
        Set<Long> favoriteProductIds = favoriteService.getFavoriteProductIds(userId)
                .stream().collect(Collectors.toSet());
        
        model.addAttribute("products", products);
        model.addAttribute("favoriteProductIds", favoriteProductIds);
        
        return "products_list";
    }

    @GetMapping("/products/new")
    public String showForm(Model model) {
        model.addAttribute("product", new Product());
        return "products_form";
    }

    /**
     * 商品登録処理 (カテゴリ対応版)
     */
    @PostMapping(value = "/products/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String addProduct(
            @RequestParam String name,
            @RequestParam int price,
            @RequestParam String category, // 【追加】
            @RequestParam MultipartFile image) {

        productService.createProduct(name, price, category, image);
        return "redirect:/products_list";
    }

    @DeleteMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            boolean success = productService.deleteProduct(id);
            return success ? ResponseEntity.ok("削除完了") : ResponseEntity.status(404).body("未検出");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("エラー: " + e.getMessage());
        }
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        return productService.findProductById(id)
            .map(p -> {
                model.addAttribute("product", p);
                return "products_edit";
            })
            .orElse("error/404");
    }

    /**
     * 商品更新処理 (カテゴリ対応版)
     */
    @PostMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("price") int price,
            @RequestParam("category") String category, // 【追加】
            @RequestParam(value = "image", required = false) MultipartFile image) {

        productService.updateProduct(id, name, price, category, image);
        return "redirect:/products_list";
    }

    @GetMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productService.findProductById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}