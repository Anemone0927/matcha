package com.example.matcha.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

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
    @JsonIgnore // 無限再帰を防ぐため、JSONシリアライズ時には親のProductへの参照を無視する
    private Product product;

    private String author;
    private String content;
    private String productName;
    
    // 評価 (intからIntegerに変更)
    private Integer rating;

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

    public String getProductName() {
            return productName;
    }

    public void setProductName(String productName) {
            this.productName = productName;
    }
    
    /**
     * 【重要: 追加したメソッド】
     * 外部のコード (ServiceやController) で呼び出されている 
     * getProductId() の呼び出しに対応するためのカスタムGetter。
     * 関連するProductエンティティからIDを抽出して返します。
     * * @return 関連する商品のID (Long)
     */
    public Long getProductId() {
        // productがnullでないことを確認してからIDを返します。
        return this.product != null ? this.product.getId() : null;
    }
    
    // 【補足】もしどこかで setProductId(Long id) が必要になった場合は、
    // ここで ProductRepository を使って Product エンティティを取得し、
    // setProduct(Product product) を呼び出す処理が必要になります。
    // 今回はコンパイルエラー解消のため getProductId() の追加のみに留めます。


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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}