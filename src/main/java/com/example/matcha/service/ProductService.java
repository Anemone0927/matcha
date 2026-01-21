package com.example.matcha.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final FavoriteRepository favoriteRepository;

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

    /**
     * 【追加】検索・フィルタリング・ソート処理
     */
    public List<Product> searchProducts(String keyword, String category, String sort) {
        List<Product> products = productRepository.findAll();

        return products.stream()
            // 1. 名前で絞り込み (keyword)
            .filter(p -> keyword == null || keyword.isEmpty() || 
                    p.getName().toLowerCase().contains(keyword.toLowerCase()))
            // 2. カテゴリで絞り込み (category)
            .filter(p -> category == null || category.isEmpty() || 
                    category.equals(p.getCategory()))
            // 3. ソート (sort)
            .sorted((p1, p2) -> {
                if ("price_asc".equals(sort)) {
                    return Integer.compare(p1.getPrice(), p2.getPrice());
                } else if ("price_desc".equals(sort)) {
                    return Integer.compare(p2.getPrice(), p1.getPrice());
                } else {
                    // デフォルトは新着順 (IDの降順)
                    return Long.compare(p2.getId(), p1.getId());
                }
            })
            .collect(Collectors.toList());
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> findProductsByIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }
        Iterable<Product> productsIterable = productRepository.findAllById(productIds);
        return StreamSupport.stream(productsIterable.spliterator(), false)
                            .collect(Collectors.toList());
    }

    /**
     * 【修正】カテゴリ(category)も保存できるように引数を追加
     */
    @Transactional
    public Product createProduct(String name, int price, String category, MultipartFile image) {
        String filename = saveImage(image);
        Product product = new Product();
        product.setName(name);
        product.setPrice(price); 
        product.setCategory(category); // カテゴリをセット
        product.setImagePath(IMAGE_PATH_PREFIX + filename);
        return productRepository.save(product);
    }

    @Transactional
    public Optional<Product> updateProduct(Long id, String name, int price, String category, MultipartFile image) {
        return productRepository.findById(id).map(product -> {
            product.setName(name);
            product.setPrice(price);
            product.setCategory(category); // カテゴリを更新
            if (image != null && !image.isEmpty()) {
                String filename = saveImage(image);
                product.setImagePath(IMAGE_PATH_PREFIX + filename);
            }
            return productRepository.save(product);
        });
    }

    @Transactional
    public boolean deleteProduct(Long id) {
        return productRepository.findById(id).map(product -> {
            favoriteRepository.deleteByProductId(id);
            orderRepository.deleteByProductId(id);
            cartItemRepository.deleteByProductId(id);
            reviewRepository.deleteByProduct_Id(id);
            productRepository.delete(product);
            deleteImageFile(product.getImagePath());
            return true;
        }).orElse(false);
    }

    private String saveImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) throw new RuntimeException("画像が空です");
        try {
            String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path path = Paths.get(uploadDir, filename);
            Files.createDirectories(path.getParent());
            Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("保存失敗", e);
        }
    }

    private void deleteImageFile(String imagePath) {
        if (imagePath != null && imagePath.startsWith(IMAGE_PATH_PREFIX)) {
            try {
                Files.deleteIfExists(Paths.get(uploadDir, imagePath.substring(IMAGE_PATH_PREFIX.length())));
            } catch (IOException e) {
                logger.error("削除エラー", e);
            }
        }
    }
}