package com.example.matcha.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

// ※ Reviewエンティティのパスが不明なため、仮に com.example.matcha.entity.Review としています

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int price;

    private String imagePath;
    
    // 💡 修正・追加箇所: OneToMany リレーションシップの設定
    // mappedBy = "product": Reviewエンティティ側のフィールド名
    // cascade = CascadeType.ALL: このProductが削除されたとき、関連するReviewも全て削除されるように設定
    // orphanRemoval = true: 関連づけが切れたReviewも自動的に削除
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Review> reviews; // 関連するレビューを保持するリスト

    // --- コンストラクタ ---
    public Product() {}

    // --- ゲッターとセッター ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    // List<Review> のゲッターとセッターも追加します
    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
