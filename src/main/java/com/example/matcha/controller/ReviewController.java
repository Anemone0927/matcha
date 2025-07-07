package com.example.matcha.controller;

import com.example.matcha.entity.Review;
import com.example.matcha.repository.ReviewRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    // 商品にレビューを追加
    @PostMapping
    public ResponseEntity<Review> addReview(@RequestBody Review review) {
        return ResponseEntity.ok(reviewRepository.save(review));
    }

    // 商品のレビューを取得
    @GetMapping("/{productId}")
    public List<Review> getReviewsByProduct(@PathVariable Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    // レビューの更新
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId, @RequestBody Review newReview) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Review review = optionalReview.get();
        review.setAuthor(newReview.getAuthor());
        review.setContent(newReview.getContent());
        review.setRating(newReview.getRating());

        reviewRepository.save(review);
        return ResponseEntity.ok("更新しました！");
    }

    // レビューの削除
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        if (reviewRepository.existsById(reviewId)) {
            reviewRepository.deleteById(reviewId);
            return ResponseEntity.ok("削除しました！");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
