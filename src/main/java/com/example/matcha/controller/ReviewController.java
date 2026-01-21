package com.example.matcha.controller;

import com.example.matcha.entity.Review;
import com.example.matcha.repository.ReviewRepository;
import com.example.matcha.repository.ProductRepository; // ProductRepositoryはAPIロジックには使われないため、必要なければ削除可能ですが、ここでは残します

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController; // @Controller + @ResponseBody の代わりに @RestController を推奨
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

/**
 * レビューの REST API エンドポイント（/api/reviews）のみを担当するコントローラーです。
 * JSONデータのみを扱います。
 */
@RestController // @Controller と @ResponseBody の組み合わせの代わりにこれを使います
@RequestMapping("/api/reviews") // すべてのエンドポイントの前に /api/reviews を付与します
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository; // 現在のAPIメソッドでは未使用ですが、将来の拡張のために残します

    // =================================================================
    // 【APIエンドポイント】
    // =================================================================

    /**
     * 【GET /{productId}】特定の商品IDに紐づく全てのレビューを取得する。(API)
     * 最終的なURL: /api/reviews/{productId}
     */
    @GetMapping("/{productId}")
    public List<Review> getReviewsByProduct(@PathVariable Long productId) {
        // ReviewRepositoryに findByProduct_Id(Long) メソッドが存在することを前提とします。
        return reviewRepository.findByProduct_Id(productId);
    }

    /**
     * 【PUT /{reviewId}】レビューを更新する。(API)
     * 最終的なURL: /api/reviews/{reviewId}
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable Long reviewId, @RequestBody Review newReview) {
        Optional<Review> existingReview = reviewRepository.findById(reviewId);
        
        if (existingReview.isEmpty()) {
            return ResponseEntity.status(404).body("指定されたIDのレビューが見つかりませんでした。");
        }
        
        Review reviewToUpdate = existingReview.get();
        
        // Nullチェックを行い、リクエストボディで提供されたフィールドのみを更新
        if (newReview.getAuthor() != null) reviewToUpdate.setAuthor(newReview.getAuthor());
        if (newReview.getContent() != null) reviewToUpdate.setContent(newReview.getContent());
        // ratingはintプリミティブ型や非Optional型の場合、newReviewで値が設定されていなくても0やnullが入る可能性があるため注意が必要です。
        //ここではWrapper型 (Integer) を仮定し、nullチェックを適用します。
        if (newReview.getRating() != null) reviewToUpdate.setRating(newReview.getRating()); 
        
        try {
            reviewRepository.save(reviewToUpdate);
            return ResponseEntity.ok("レビューを更新しました！");
        } catch (Exception e) {
            System.err.println("レビュー更新エラー: " + e.getMessage());
            return ResponseEntity.status(500).body("レビューの更新中にサーバーエラーが発生しました。");
        }
    }

    /**
     * 【DELETE /{reviewId}】レビューを削除する。(API)
     * 最終的なURL: /api/reviews/{reviewId}
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        try {
             if (!reviewRepository.existsById(reviewId)) {
                 return ResponseEntity.status(404).body("指定されたIDのレビューが見つかりませんでした。");
             }
             reviewRepository.deleteById(reviewId);
             return ResponseEntity.ok("レビューを削除しました！");
        } catch (Exception e) {
             System.err.println("レビュー削除エラー: " + e.getMessage());
             return ResponseEntity.status(500).body("レビューの削除中にサーバーエラーが発生しました: " + e.getMessage());
        }
    }
}