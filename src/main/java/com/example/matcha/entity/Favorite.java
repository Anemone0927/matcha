package com.example.matcha.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * お気に入り情報を保持するエンティティクラス
 * ユーザーIDと商品IDの組み合わせを保存します。
 * * 注意: 現時点ではユーザー認証をスキップし、ユーザーIDを固定値 (1L) で扱います。
 * 実際には、Spring Securityなどを導入して認証済みのユーザーIDを使用する必要があります。
 */
@Entity
@Table(name = "favorites")
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ユーザーID (今回は簡略化のため固定値を使用)
    private Long userId;

    // 商品エンティティへの関連付け
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // コンストラクタ
    public Favorite() {}

    public Favorite(Long userId, Product product) {
        this.userId = userId;
        this.product = product;
    }

    // ゲッターとセッター
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}