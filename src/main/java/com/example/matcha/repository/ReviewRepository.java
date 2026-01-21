package com.example.matcha.repository;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.matcha.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * 【修正】商品IDでレビューを検索するためのメソッド。
     * ReviewエンティティはProductオブジェクト（フィールド名: product）を持つため、
     * Spring Data JPAの命名規則に従い、'Product_Id' (リレーションシップ名 + ID) を使用します。
     * @param productId 検索対象の商品ID
     * @return 該当するレビューのリスト
     */
    List<Review> findByProduct_Id(Long productId);

    /**
     * 【修正】商品IDに紐づくすべてのレビューを削除するためのメソッド。
     * 同様に 'Product_Id' を使用します。
     * @param productId 削除対象の商品ID
     */
    @Transactional // 削除操作にはトランザクションが必要
    void deleteByProduct_Id(Long productId);
}