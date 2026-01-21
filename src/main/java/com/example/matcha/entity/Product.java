package com.example.matcha.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private Integer price;

    private String category; // 【追加】カテゴリ（抹茶、お菓子、茶器など）

    @Column(name = "image_path")
    private String imagePath;

    // 【重要】お気に入り状態を保持。@Transientを付けるとDBのテーブルには作成されません。
    @Transient
    private boolean isFavorited;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> reviews;

    public Product() {}

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public String getCategory() { return category; } // 【追加】
    public void setCategory(String category) { this.category = category; } // 【追加】

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public boolean isFavorited() { return isFavorited; } // 【追加】
    public void setFavorited(boolean favorited) { isFavorited = favorited; } // 【追加】

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
}