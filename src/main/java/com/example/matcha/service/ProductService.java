package com.example.matcha.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
// import java.util.Set; // 削除
// import java.util.stream.Collectors; // 削除

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

// import com.example.matcha.entity.Favorite; // 削除
import com.example.matcha.entity.Product;
import com.example.matcha.repository.CartItemRepository;
import com.example.matcha.repository.FavoriteRepository;
import com.example.matcha.repository.OrderRepository;
import com.example.matcha.repository.ProductRepository;
import com.example.matcha.repository.ReviewRepository;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    @Value("${upload.dir}")
    private String uploadDir;

    private static final String IMAGE_PATH_PREFIX = "/images/";

    // 定数: お気に入りロジックはFavoriteServiceに移動したため削除
    // private static final Long DEFAULT_USER_ID = 1L; 

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final FavoriteRepository favoriteRepository;

    /**
     * コンストラクタインジェクション
     */
    public ProductService(
        ProductRepository productRepository, 
        ReviewRepository reviewRepository, 
        CartItemRepository cartItemRepository,
        OrderRepository orderRepository,
        FavoriteRepository favoriteRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.favoriteRepository = favoriteRepository;
    }

    // --- Product CRUD Operations ---

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    /**
     * 商品IDから商品エンティティを取得する。
     * FavoriteServiceから呼び出されるため、このメソッドは維持します。
     */
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * 商品の新規登録処理
     */
    public Product createProduct(String name, int price, MultipartFile image) {
        String filename = saveImage(image);

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setImagePath(IMAGE_PATH_PREFIX + filename);

        return productRepository.save(product);
    }
    
    /**
     * 商品の更新処理 (画像はオプション)
     */
    public Optional<Product> updateProduct(Long id, String name, int price, MultipartFile image) {
        Optional<Product> optProduct = productRepository.findById(id);
        
        if (optProduct.isPresent()) {
            Product product = optProduct.get();
            
            product.setName(name);
            product.setPrice(price);

            if (image != null && !image.isEmpty()) {
                // 古い画像を削除するロジックは、必要に応じて追加してください
                String filename = saveImage(image);
                product.setImagePath(IMAGE_PATH_PREFIX + filename);
            }
            
            return Optional.of(productRepository.save(product));
        }
        return Optional.empty();
    }

    /**
     * 商品削除処理（トランザクション管理、関連データ削除、画像ファイル削除を含む）
     */
    @Transactional
    public boolean deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            logger.warn("商品ID: {} は存在しないため削除できませんでした。", id);
            return false;
        }

        Product product = optionalProduct.get();
        String imagePath = product.getImagePath();
        
        try {
            // 0. お気に入りテーブル (favorites) の関連レコードを先に削除 (FavoriteServiceからRepositoryを直接使用)
            favoriteRepository.deleteByProductId(id);
            logger.info("商品ID: {} に関連するお気に入りレコードを全て削除しました。", id);

            // 1. 注文テーブル (orders) の関連レコードを先に削除
            orderRepository.deleteByProductId(id);
            logger.info("商品ID: {} に関連する注文レコードを全て削除しました。", id);

            // 2. 関連する CartItem を全て削除する
            cartItemRepository.deleteByProductId(id);
            logger.info("商品ID: {} に関連するカートアイテムを全て削除しました。", id);

            // 3. 関連する Review を全て削除する
            reviewRepository.deleteByProductId(id);
            logger.info("商品ID: {} に関連するレビューを全て削除しました。", id);

            // 4. データベースのレコードを削除
            productRepository.delete(product);
            logger.info("商品ID: {} がデータベースから削除されました。", id);

            // 5. サーバー上の画像ファイルを削除
            deleteImageFile(imagePath);
            
            return true;
        } catch (Exception e) {
            logger.error("商品ID: {} の削除処理中にエラーが発生しました。データベース操作はロールバックされます。", id, e);
            throw new RuntimeException("商品の削除に失敗しました: " + e.getMessage(), e);
        }
    }


    // --- お気に入り関連のサービスロジック (Favorite Logic) はFavoriteServiceに移動しました ---


    // --- File Handling Methods ---

    /**
     * 画像をサーバーに保存し、ファイル名を返すプライベートメソッド
     */
    private String saveImage(MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            throw new RuntimeException("画像ファイルが空です。");
        }
        try {
            String originalFilename = imageFile.getOriginalFilename();
            String baseFilename = originalFilename != null ? originalFilename : "no_name_file";
            
            String filename = UUID.randomUUID() + "_" + baseFilename;
            Path path = Paths.get(uploadDir, filename);
            
            Files.createDirectories(path.getParent());
            
            Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            logger.error("画像の保存に失敗しました", e);
            throw new RuntimeException("画像の保存に失敗しました", e);
        }
    }

    /**
     * サーバー上の画像ファイルを削除するプライベートメソッド
     */
    private void deleteImageFile(String imagePath) {
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
                logger.error("画像ファイル {} の削除中にIOエラーが発生しました。", filename, e);
            }
        }
    }
}