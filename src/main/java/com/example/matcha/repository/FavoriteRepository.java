package com.example.matcha.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.example.matcha.entity.Favorite;
import com.example.matcha.entity.Product;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserId(Long userId);

    Optional<Favorite> findByUserIdAndProduct(Long userId, Product product);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    @Transactional
    void deleteByProductId(Long productId);
    Favorite findByUserIdAndProductId(Long userId, Long productId);
}