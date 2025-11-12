package com.example.matcha.repository;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.matcha.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);

    @Transactional // 削除操作にはトランザクションが必要
    void deleteByProductId(Long productId);
}