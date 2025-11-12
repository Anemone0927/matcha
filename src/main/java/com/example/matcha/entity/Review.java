package com.example.matcha.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // 【追加】JsonIgnoreをインポート

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
// import java.util.List; // 不要なので削除

/**
 * レビューエンティティ
 * 商品(Product)との間に多対一 (Many-to-One) の関係を持つ
 */
@Entity
@Table(name = "reviews") // データベースのテーブル名を明示
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 多対一 (ManyToOne): 複数のレビューが1つの商品に紐づく
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false) // 外部キーカラムを "product_id" として設定
    @JsonIgnore // 【修正】無限再帰を防ぐため、JSONシリアライズ時には親のProductへの参照を無視する
    private Product product;

    private String author;
    private String content;
    private int rating;

    // --- Getter & Setter ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}