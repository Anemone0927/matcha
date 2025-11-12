package com.example.matcha.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // @Transactional を使用するためにインポート

import com.example.matcha.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // ユーザーIDに基づいてカートアイテムのリストを取得する
    List<CartItem> findByUserId(String userId);

    // ユーザーIDと商品IDに基づいて特定のカートアイテムを取得する
    Optional<CartItem> findByUserIdAndProductId(String userId, Long productId);

    /**
     * 【🚨 追加・修正点】
     * 指定された商品IDを持つ全てのカートアイテムを削除します。
     * 商品削除時の外部キー制約違反を避けるためにProductServiceから呼び出されます。
     * deleteBy... メソッドはトランザクション内で実行する必要があります。
     * * @param productId 削除対象の商品ID
     */
    @Transactional
    void deleteByProductId(Long productId);
}