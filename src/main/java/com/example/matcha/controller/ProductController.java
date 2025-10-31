package com.example.matcha.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.transaction.annotation.Transactional;

import com.example.matcha.entity.Product;
import com.example.matcha.repository.ProductRepository;

@Controller
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    // application.properties (または yml) で設定されたアップロードディレクトリのパス
    @Value("${upload.dir}")
    private String uploadDir;

    // 画像URLのプレフィックス (例: /images/)
    private static final String IMAGE_PATH_PREFIX = "/images/";

    private final ProductRepository productRepository;
    
    /**
     * 推奨されるコンストラクタインジェクション
     */
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 商品一覧API (JSONで返す)
    @GetMapping("/api/products")
    @ResponseBody
    public List<Product> getAllProducts() {
        logger.info("APIエンドポイント /api/products が呼び出されました。");
        return productRepository.findAll();
    }

    // 商品一覧画面表示（Thymeleafでrender）
    @GetMapping("/products_list")
    public String showList(Model model) {
        logger.info("Viewエンドポイント /products_list が呼び出されました。");
        // 商品データの取得はJavaScript側（/api/products）に任せるため、ここではViewを返すのみ
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

        String filename = saveImage(image);

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setImagePath(IMAGE_PATH_PREFIX + filename); // 定数を使用

        productRepository.save(product);
        logger.info("新しい商品が登録されました: {}", name);

        return "redirect:/products_list";
    }

    // 画像保存メソッド
    private String saveImage(MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            // 画像アップロードが必須でない場合は、ここで空のパスを返すなどの対応が必要ですが、
            // products/new の場合は必須としてRuntime Exceptionをスローします。
            throw new RuntimeException("画像ファイルが空です。");
        }
        try {
            // ファイル名が重複しないようにUUIDを付与
            String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path path = Paths.get(uploadDir, filename);
            
            // ディレクトリが存在しない場合は作成
            Files.createDirectories(path.getParent());
            
            // ファイルを保存
            Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            logger.error("画像の保存に失敗しました", e);
            throw new RuntimeException("画像の保存に失敗しました", e);
        }
    }

    // 💡 修正箇所: 商品削除（DBレコードとサーバー上の画像ファイルを削除する）
    @DeleteMapping("/products/{id}")
    @ResponseBody
    @Transactional 
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            logger.warn("商品ID: {} は存在しないため削除できませんでした。", id);
            return ResponseEntity.notFound().build();
        }

        Product product = optionalProduct.get();
        String imagePath = product.getImagePath();
        
        // 💡 削除メッセージ用に商品名を保持
        String productName = product.getName(); 
        
        // --- 1. サーバー上の画像ファイルを削除 (失敗してもDB削除は継続) ---
        if (imagePath != null && imagePath.startsWith(IMAGE_PATH_PREFIX)) {
            String filename = imagePath.substring(IMAGE_PATH_PREFIX.length());
            Path filePath = Paths.get(uploadDir, filename);
            
            try {
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    logger.info("関連画像ファイルが削除されました: {}", filename);
                } else {
                    logger.warn("関連画像ファイルが見つかりませんでした: {}", filename);
                }
            } catch (IOException e) {
                logger.error("画像ファイル {} の削除中にIOエラーが発生しましたが、DB削除を試行します。", filename, e);
            }
        }
        
        // --- 2. データベースのレコードを削除 ---
        try {
            // @Transactionalがあるため、関連するReviewも削除されるはず
            productRepository.delete(product);
            logger.info("商品ID: {} がデータベースから削除されました。", id);
            
            // 💡 成功メッセージを修正: 商品名を含める
            return ResponseEntity.ok("商品: " + productName + " を削除しました！");
            
        } catch (Exception e) {
            // DB削除（カスケード削除含む）でエラーが発生した場合
            logger.error("商品ID: {} のデータベース削除処理中にエラーが発生しました。", id, e);
            // 削除処理中にDBエラーが発生した場合、500 Internal Server Errorを返す
            return ResponseEntity.internalServerError().body("データベース削除中にエラーが発生しました: " + e.getMessage());
        }
    }

    // 商品詳細取得（API的にJSONで返す）
    @GetMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
            // 商品が存在すれば200 OKとProduct、存在しなければ404 Not Foundを返す
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // 商品編集フォーム画面表示 (GET /products/edit/{id})
    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            // 編集対象の商品をモデルに格納
            model.addAttribute("product", product.get());
            
            // products_edit.html (Thymeleafテンプレート) を返す
            return "products_edit"; 
        } else {
            // 商品が見つからなかった場合は404エラービューを返す
            return "error/404"; 
        }
    }
    
    // 商品更新処理 (POST /products/{id}) - このメソッドが products_edit.html のフォーム送信を受けます
    @PostMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateProduct(
        @PathVariable Long id,
        @RequestParam("name") String name,
        @RequestParam("price") int price,
        // 画像はオプション (required = false) とし、null許容にする
        @RequestParam(value = "image", required = false) MultipartFile image,
        Model model) {

        Optional<Product> optProduct = productRepository.findById(id);
        if (optProduct.isPresent()) {
            Product product = optProduct.get();
            
            // 商品名と価格を更新
            product.setName(name);
            product.setPrice(price);

            // 画像がアップロードされた場合のみ、画像を保存し、パスを更新する
            if (image != null && !image.isEmpty()) {
                // 既存の画像を削除するロジックをここに含めることも可能だが、
                // 今回は新規画像がアップロードされた場合のみ上書き（クリーンアップを簡略化）
                
                String filename = saveImage(image);
                product.setImagePath(IMAGE_PATH_PREFIX + filename);
            }
            
            // データベースに保存
            productRepository.save(product);
            logger.info("商品ID: {} が更新されました。", id);
        }

        return "redirect:/products_list";
    }
}
