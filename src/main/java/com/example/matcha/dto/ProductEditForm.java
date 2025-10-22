package com.example.matcha.dto;

import org.springframework.web.multipart.MultipartFile;

// 商品編集フォームで扱うデータを格納するDTO (Data Transfer Object)
public class ProductEditForm {

    private Long id;
    private String name;
    private Long price; // 価格
    private String imagePath; // 現在の画像パス
    private MultipartFile image; // 新しい画像ファイル

    // --- コンストラクタ ---
    public ProductEditForm() {}

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

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
