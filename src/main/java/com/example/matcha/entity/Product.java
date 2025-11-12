package com.example.matcha.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items") // テーブル名が cart_items だと仮定
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 💡 エラーの原因: CartControllerで必要とされるフィールド
    @Column(name = "product_id")
    private Long productId; 
    
    private Integer quantity;

    // Constructors
    public CartItem() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // 💡 CartController のエラーを解消する必須メソッド
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}