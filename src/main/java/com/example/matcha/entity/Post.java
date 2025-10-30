package com.example.matcha.entity; // 💡 パッケージ名を 'entity' に変更

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 投稿（Post）エンティティ
 * データベースの 'posts' テーブルに対応します。
 */
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主キー (自動生成)

    private String caption; // 投稿のキャプション（説明文）

    // 物理ファイル名を保存するためのカラム
    private String imageFileName; 

    // デフォルトコンストラクタ（JPAが必要とします）
    public Post() {}

    // ゲッターとセッター

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    // 💡 物理ファイル操作に必須のゲッター
    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
}
