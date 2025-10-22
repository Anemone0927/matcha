package com.example.matcha.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table; // データベースに合わせてこのTableアノテーションも確認

@Entity
@Table(name = "products") // データベースのテーブル名と一致しているか確認
public class Product {

    // 💡 必須: IDフィールド
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PostgreSQL (bigserial) / H2 DBで自動採番を使うための設定

    // 💡 必須: 商品名
    private String name;

    // 💡 必須: 価格
    private int price;

    // 💡 必須: 画像パス
    private String imagePath;

    // --- コンストラクタ ---
    public Product() {}

    // --- ゲッターとセッター ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
