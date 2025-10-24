package com.example.matcha.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne; // 追記
import jakarta.persistence.JoinColumn; // 追記

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 💡 修正点: Long productId を削除し、@ManyToOne の Product 参照に置き換え
    @ManyToOne 
    @JoinColumn(name = "product_id", nullable = false) 
    private Product product; // このフィールド名が Product.java の mappedBy="product" に対応

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

    // 💡 修正点: getProductId/setProductId を getProduct/setProduct に変更
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
