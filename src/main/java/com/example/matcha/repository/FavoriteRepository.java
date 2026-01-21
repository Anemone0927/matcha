package com.example.matcha.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.matcha.entity.Favorite;
import jakarta.transaction.Transactional; // トランザクションが必要なメソッドのため

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * 特定のユーザーのお気に入りリストを取得
     */
    List<Favorite> findByUserId(Long userId);

    /**
     * 特定のユーザーと商品の組み合わせがお気に入り登録されているかチェック (Serviceで利用)
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    /**
     * 特定のユーザーと商品の組み合わせのお気に入りレコードを削除 (Serviceで利用)
     */
    @Transactional 
    void deleteByUserIdAndProductId(Long userId, Long productId);

    /**
     * 商品IDを指定して全てのお気に入りレコードを削除 (商品削除時などに利用可能)
     */
    @Transactional
    void deleteByProductId(Long productId);
}