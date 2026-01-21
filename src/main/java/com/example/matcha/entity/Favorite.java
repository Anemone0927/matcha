package com.example.matcha.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
// import lombok.Data; // 削除！

@Entity
@Table(name = "favorite") // テーブル名: favorite
// @Data // 削除！
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // プライマリキー

    // どのユーザーがお気に入り登録したか
    private Long userId;

    // どの商品をお気に入り登録したか
    private Long productId;

    // --- 手動でGetter/Setterを追加 (これによりコンパイルエラーを回避) ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}