package com.example.matcha.service;

import com.example.matcha.entity.Product;
import com.example.matcha.entity.Review;
import com.example.matcha.repository.ProductRepository;
import com.example.matcha.repository.ReviewRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // トランザクションをインポート

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    // --- 【追加】商品名付きで全レビューを取得するメソッド ---
    /**
     * 全てのレビューを取得し、対応する商品名 (productName) を各Reviewオブジェクトにセットします。
     * * @return 商品名がセットされたReviewエンティティのリスト
     */
    public List<Review> findAllReviewsWithProductName() {
        // 1. 全レビューを取得
        List<Review> reviews = reviewRepository.findAll();

        // 2. 各レビューに対して商品名を紐づける
        for (Review review : reviews) {
            // Reviewエンティティに設定されている Product リレーションからIDを取得
            
            // ProductRepositoryから商品情報を取得
            Optional<Product> productOpt = productRepository.findById(review.getProduct().getId());
            
            if (productOpt.isPresent()) {
                // Productエンティティから商品名を取得し、ReviewエンティティのproductNameにセット
                review.setProductName(productOpt.get().getName());
            } else {
                // 商品が見つからなかった場合のフォールバック
                review.setProductName("商品情報が見つかりません");
            }
        }
        
        return reviews;
    }
    // -------------------------------------------------------------

    /**
     * Reviewオブジェクトを受け取り、対応するProductと紐づけてデータベースに保存します。
     * @param review 保存対象のReviewエンティティ（productIdが設定されていること）
     * @return 保存されたReviewエンティティ
     * @throws NoSuchElementException 指定された商品IDが存在しない場合
     */
    @Transactional // 保存処理にトランザクションを追加
    public Review saveReview(Review review) {
        
        // 1. 商品IDからProductエンティティを取得
        Long productId = review.getProduct().getId();
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("商品ID: " + productId + " が見つかりません。"));

        // 2. レビューエンティティにリレーションシップを設定
        review.setProduct(product);
        
        // 3. 保存を実行
        return reviewRepository.save(review);
    }
    
    // --- 【追加】レビュー削除メソッド (最も重要な修正) ---
    /**
     * 指定されたIDのレビューをデータベースから削除します。
     * @param reviewId 削除対象のレビューID
     * @throws NoSuchElementException 指定されたレビューIDが存在しない場合
     */
    @Transactional // 削除処理にトランザクションを追加
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            // 削除対象が存在しない場合は例外をスロー
            throw new NoSuchElementException("レビューID: " + reviewId + " が見つかりません。");
        }
        reviewRepository.deleteById(reviewId);
    }
    // -------------------------------------------------------------

    /**
     * 特定の商品IDに紐づく全てのレビューを取得する。
     * @param productId 商品ID
     * @return レビューのリスト
     */
    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProduct_Id(productId);
    }
}