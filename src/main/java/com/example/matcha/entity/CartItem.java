package com.example.matcha.entity;



import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;

import jakarta.persistence.GenerationType;

import jakarta.persistence.Id;

import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;

import jakarta.persistence.Table;



@Entity

@Table(name = "cart_items")

public class CartItem {



    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;



    // 💡 必要なフィールド 1: ユーザーID (setUserIdのために必須)

    private String userId; 

    

    // 💡 必要なフィールド 2: 商品情報 (getProductのために必須)

    @ManyToOne 

    @JoinColumn(name = "product_id")

    private Product product; 



    private int quantity;



    // --- コンストラクタ ---

    public CartItem() {

    }



    // --- ゲッターとセッター ---

    public Long getId() {

        return id;

    }



    public void setId(Long id) {

        this.id = id;

    }



    // 💡 setUserId の定義

    public String getUserId() {

        return userId;

    }



    public void setUserId(String userId) {

        this.userId = userId;

    }



    // 💡 getProduct の定義

    public Product getProduct() {

        return product;

    }



    public void setProduct(Product product) {

        this.product = product;

    }



    public int getQuantity() {

        return quantity;

    }



    public void setQuantity(int quantity) {

        this.quantity = quantity;

    }

}