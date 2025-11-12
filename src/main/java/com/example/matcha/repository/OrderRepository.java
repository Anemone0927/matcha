package com.example.matcha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.matcha.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 指定された商品IDに関連付けられている注文レコードを全て削除します。
     * 外部キー制約を回避するため、ネイティブSQLでテーブル名とカラム名を直接指定します。
     * @param productId 削除対象のProduct ID
     */
    @Modifying
    @Transactional
    // 【★修正点: nativeQuery = true を追加し、HQLからネイティブSQLへ変更】
    @Query(value = "DELETE FROM orders WHERE product_id = :productId", nativeQuery = true)
    void deleteByProductId(@Param("productId") Long productId);
}