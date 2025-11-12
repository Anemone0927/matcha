package com.example.matcha.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set; // 【★追加】

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // 【★追加】
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.matcha.entity.Product;
import com.example.matcha.service.ProductService;

@Controller
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    /**
     * コンストラクタインジェクション (ProductServiceのみを注入)
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 商品一覧API (JSONで返す)
    @GetMapping("/api/products")
    @ResponseBody
    public List<Product> getAllProducts() {
        logger.info("APIエンドポイント /api/products が呼び出されました。");
        return productService.findAllProducts();
    }

    // 商品一覧画面表示（Thymeleafでrender）
    @GetMapping("/products_list")
    public String showList(Model model) {
        logger.info("Viewエンドポイント /products_list が呼び出されました。");
        
        // 1. 商品リストの取得
        List<Product> products = productService.findAllProducts();
        model.addAttribute("products", products);

        // 2. 【★追加: お気に入り商品IDリストの取得】
        Set<Long> favoriteProductIds = productService.getFavoriteProductIdsForCurrentUser();
        model.addAttribute("favoriteProductIds", favoriteProductIds);
        
        return "products_list";
    }

    // 商品追加フォーム画面表示
    @GetMapping("/products/new")
    public String showForm(Model model) {
        model.addAttribute("product", new Product());
        return "products_form";
    }

    // 商品追加処理（画像アップロード含む）
    @PostMapping(value = "/products/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String addProduct(
        @RequestParam String name,
        @RequestParam int price,
        @RequestParam MultipartFile image,
        Model model) {

        Product newProduct = productService.createProduct(name, price, image);
        logger.info("新しい商品が登録されました: {}", newProduct.getName());

        return "redirect:/products_list";
    }

    // 商品削除（サービス層に処理を委譲）
    // ※ 既存のDELETEマッピングはそのまま残しつつ、HTMLのPOSTフォームでDELETEをエミュレートすることも考慮し、
    //    今回はHTML側のフォームに合わせて/products/delete/{id}のPOSTマッピングも持たせている可能性があります。
    @DeleteMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            boolean success = productService.deleteProduct(id);

            if (success) {
                logger.info("商品ID: {} の削除が完了しました。", id);
                return ResponseEntity.ok("商品ID: " + id + " を削除しました！");
            } else {
                logger.warn("商品ID: {} の削除に失敗しました (商品が存在しませんでした)。", id);
                return ResponseEntity.status(404).body("指定された商品IDが見つかりませんでした。");
            }
        } catch (RuntimeException e) {
             logger.error("商品ID: {} の削除処理中に予期せぬエラーが発生しました。", id, e);
             return ResponseEntity.internalServerError().body("削除処理中にエラーが発生しました: " + e.getMessage());
        }
    }

    // 商品詳細取得（API的にJSONで返す）
    @GetMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productService.findProductById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // 商品編集フォーム画面表示 (GET /products/edit/{id})
    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.findProductById(id);
        
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "products_edit";
        } else {
            return "error/404";
        }
    }
    
    // 商品更新処理 (POST /products/{id})
    @PostMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateProduct(
        @PathVariable Long id,
        @RequestParam("name") String name,
        @RequestParam("price") int price,
        @RequestParam(value = "image", required = false) MultipartFile image,
        Model model) {

        productService.updateProduct(id, name, price, image);
        logger.info("商品ID: {} の更新リクエストが完了しました。", id);

        return "redirect:/products_list";
    }
    
    // --- 【★新規追加: お気に入りAPIエンドポイント】 ---

    /**
     * 指定された商品をお気に入りに追加します。
     * @param productId 商品ID
     * @return 成功ステータス
     */
    @PostMapping("/favorites/add/{productId}")
    public ResponseEntity<Void> addToFavorites(@PathVariable Long productId) {
        logger.info("お気に入り追加リクエスト: {}", productId);
        boolean success = productService.addToFavorites(productId);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            // 商品が見つからないなどのエラー
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 指定された商品をお気に入りから削除します。
     * @param productId 商品ID
     * @return 成功ステータス
     */
    @PostMapping("/favorites/remove/{productId}")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long productId) {
        logger.info("お気に入り削除リクエスト: {}", productId);
        boolean success = productService.removeFromFavorites(productId);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            // 処理が失敗した場合
            return ResponseEntity.badRequest().build();
        }
    }
    // ------------------------------------------------
}