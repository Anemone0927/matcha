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
     * Productを削除する際の外部キー制約違反を防ぐために、このメソッドが必要です。
     *
     * @param productId 削除対象のProduct ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Order o WHERE o.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}