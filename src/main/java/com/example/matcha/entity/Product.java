package com.example.matcha.entity;

import java.util.List; // 💡 追記: Listをインポート
import jakarta.persistence.CascadeType; // 💡 追記: カスケードタイプをインポート
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany; // 💡 追記: OneToManyをインポート

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int price;
    private String imagePath;
    
    // 💡 追記: ここが最重要！カスケード削除の設定
    // mappedBy="product" は Review.java の private Product product; のフィールド名と一致させる
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews; // レビュー一覧を保持するフィールド
    
    // --- Getter & Setter (既存のフィールドに対応) ---

    public Long getId() {
        return id;
    }
    // ... (既存のgetId, setId, getName, setName, getPrice, setPrice, getImagePath, setImagePath)
    
    // --- 💡 追記: reviews の Getter & Setter ---
    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
    
    // その他の既存のGetter & Setter
    public void setId(Long id) {
        this.id = id;
    }
    // ... (既存のコードを維持)
}
