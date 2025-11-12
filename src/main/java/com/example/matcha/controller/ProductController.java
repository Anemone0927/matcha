package com.example.matcha.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
// org.springframework.transaction.annotation.Transactional は不要になったため削除

import com.example.matcha.entity.Product;
// ProductRepository, ReviewRepository はサービス層でのみ使用するため削除
import com.example.matcha.service.ProductService; // 💡 ProductServiceをインポート

@Controller
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    // uploadDir や IMAGE_PATH_PREFIX の定義はサービス層に移動

    private final ProductService productService; // 💡 サービスを注入

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
        // 💡 サービス層に処理を委譲
        return productService.findAllProducts();
    }

    // 商品一覧画面表示（Thymeleafでrender）
    @GetMapping("/products_list")
    public String showList(Model model) {
        logger.info("Viewエンドポイント /products_list が呼び出されました。");
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

        // 💡 サービス層に処理を委譲
        Product newProduct = productService.createProduct(name, price, image);
        logger.info("新しい商品が登録されました: {}", newProduct.getName());

        return "redirect:/products_list";
    }

    // saveImage メソッドはサービス層に移動したため削除

    // 💡 修正箇所: 商品削除（サービス層に処理を委譲）
    @DeleteMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        // 💡 サービス層のdeleteProductを呼び出す
        boolean success = productService.deleteProduct(id);

        if (success) {
            logger.info("商品ID: {} の削除が完了しました。", id);
            return ResponseEntity.ok("商品ID: " + id + " を削除しました！");
        } else {
            logger.warn("商品ID: {} の削除に失敗しました (商品が存在しませんでした)。", id);
            return ResponseEntity.notFound().body("指定された商品IDが見つかりませんでした。");
        }
    }

    // 商品詳細取得（API的にJSONで返す）
    @GetMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        // 💡 サービス層に処理を委譲
        return productService.findProductById(id)
            // 商品が存在すれば200 OKとProduct、存在しなければ404 Not Foundを返す
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // 商品編集フォーム画面表示 (GET /products/edit/{id})
    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        // 💡 サービス層に処理を委譲
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
        // 画像はオプション (required = false) とし、null許容にする
        @RequestParam(value = "image", required = false) MultipartFile image,
        Model model) {

        // 💡 サービス層に処理を委譲
        productService.updateProduct(id, name, price, image);
        logger.info("商品ID: {} の更新リクエストが完了しました。", id);

        return "redirect:/products_list";
    }
}