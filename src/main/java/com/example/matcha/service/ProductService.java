package com.example.matcha.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.Set; // 【★追加】
import java.util.UUID;
import java.util.stream.Collectors; // 【★追加】

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.matcha.entity.Favorite; // 【★追加】
import com.example.matcha.entity.Product;
import com.example.matcha.repository.CartItemRepository;
import com.example.matcha.repository.FavoriteRepository; // 【★追加】
import com.example.matcha.repository.OrderRepository;
import com.example.matcha.repository.ProductRepository;
import com.example.matcha.repository.ReviewRepository;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    @Value("${upload.dir}")
    private String uploadDir;

    private static final String IMAGE_PATH_PREFIX = "/images/";

    // 定数: 今回はユーザー認証がないため、固定のユーザーIDを使用します。
    // 実際には認証情報から取得する必要があります。
    private static final Long DEFAULT_USER_ID = 1L; // 【★追加】

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final FavoriteRepository favoriteRepository; // 【★追加: FavoriteRepositoryフィールド】

    /**
     * コンストラクタインジェクション
     */
    public ProductService(
        ProductRepository productRepository, 
        ReviewRepository reviewRepository, 
        CartItemRepository cartItemRepository,
        OrderRepository orderRepository,
        FavoriteRepository favoriteRepository) { // 【★修正: FavoriteRepositoryを引数に追加】
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.favoriteRepository = favoriteRepository; // 【★追加: FavoriteRepository初期化】
    }

    // --- Product CRUD Operations ---

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

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
                String filename = saveImage(image);
                product.setImagePath(IMAGE_PATH_PREFIX + filename);
            }
            
            return Optional.of(productRepository.save(product));
        }
        return Optional.empty();
    }

    /**
     * 商品削除処理（トランザクション管理、関連データ削除、画像ファイル削除を含む）
     * @param id 削除する商品のID
     * @return 削除が成功した場合は true
     */
    @Transactional // サービス層のビジネスロジックとしてトランザクションを管理
    public boolean deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            logger.warn("商品ID: {} は存在しないため削除できませんでした。", id);
            return false;
        }

        Product product = optionalProduct.get();
        String imagePath = product.getImagePath();
        
        try {
            // 【★追加: 0. お気に入りテーブル (favorites) の関連レコードを先に削除】
            favoriteRepository.deleteByProductId(id);
            logger.info("商品ID: {} に関連するお気に入りレコードを全て削除しました。", id);

            // 【1. 注文テーブル (orders) の関連レコードを先に削除】
            orderRepository.deleteByProductId(id);
            logger.info("商品ID: {} に関連する注文レコードを全て削除しました。", id);

            // --- 2. 関連する CartItem を全て削除する (外部キー制約の回避 その1) ---
            cartItemRepository.deleteByProductId(id);
            logger.info("商品ID: {} に関連するカートアイテムを全て削除しました。", id);

            // --- 3. 関連する Review を全て削除する (外部キー制約の回避 その2) ---
            reviewRepository.deleteByProductId(id);
            logger.info("商品ID: {} に関連するレビューを全て削除しました。", id);

            // --- 4. データベースのレコードを削除 ---
            productRepository.delete(product);
            logger.info("商品ID: {} がデータベースから削除されました。", id);

            // --- 5. サーバー上の画像ファイルを削除 ---
            deleteImageFile(imagePath);
            
            return true;
        } catch (Exception e) {
            logger.error("商品ID: {} の削除処理中にエラーが発生しました。データベース操作はロールバックされます。", id, e);
            throw new RuntimeException("商品の削除に失敗しました: " + e.getMessage(), e);
        }
    }


    // --- お気に入り関連のサービスロジック (Favorite Logic) --- // 【★新規追加】

    /**
     * 現在のユーザーのお気に入り商品IDのセットを取得します。
     * @return お気に入りに登録されている商品IDのSet
     */
    public Set<Long> getFavoriteProductIdsForCurrentUser() {
        // DEFAULT_USER_ID を使ってユーザーのお気に入り情報を取得
        List<Favorite> favorites = favoriteRepository.findByUserId(DEFAULT_USER_ID);
        
        // お気に入りリストから商品IDのみを抽出し、Setにして返却
        return favorites.stream()
                .map(favorite -> favorite.getProduct().getId())
                .collect(Collectors.toSet());
    }

    /**
     * 指定された商品をお気に入りに追加します。
     * @param productId 商品ID
     * @return 成功した場合true
     */
    @Transactional
    public boolean addToFavorites(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return false;
        }

        Product product = productOpt.get();
        // 既にお気に入り登録済みかチェック
        if (favoriteRepository.findByUserIdAndProduct(DEFAULT_USER_ID, product).isPresent()) {
            return true; // 既に登録済みなら成功とみなす
        }

        Favorite favorite = new Favorite(DEFAULT_USER_ID, product);
        favoriteRepository.save(favorite);
        return true;
    }

    /**
     * 指定された商品をお気に入りから削除します。
     * @param productId 商品ID
     * @return 成功した場合true
     */
    @Transactional
    public boolean removeFromFavorites(Long productId) {
        // Repositoryのカスタムメソッド deleteByUserIdAndProductId を使用
        favoriteRepository.deleteByUserIdAndProductId(DEFAULT_USER_ID, productId);
        return true;
    }

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