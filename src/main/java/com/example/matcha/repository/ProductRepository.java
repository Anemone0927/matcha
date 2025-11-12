package com.example.matcha.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.matcha.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 【🚨 追加・修正点】
     * 全ての商品を取得する際に、レビュー情報もJOINして一度に取得することで
     * N+1問題を解消し、データベースへのクエリ回数を削減します。
     * @return 商品と関連レビューが全て含まれたリスト
     */
    @Override
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.reviews")
    List<Product> findAll();
}