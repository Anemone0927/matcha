package com.example.matcha.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.matcha.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
}
