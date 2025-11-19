package com.example.matcha.dto;

import com.example.matcha.entity.CartItem;

/**
 * カート一覧画面（フロントエンド）に送信するためのデータ転送オブジェクト (DTO)。
 * JSが price, quantity, imagePath を期待しているため、この構造で定義します。
 */
public class CartItemDto {
    
    private Long id; // カートアイテムID (CartItemのID)
    private String name; // 商品名
    private Integer price; // 商品の単価 (Productのprice)
    private Integer quantity; // 数量 (CartItemのquantity)
    private String imagePath; // 画像パス (ProductのimagePath)

    // デフォルトコンストラクタ（Spring/Jackson用）
    public CartItemDto() {}

    // 全フィールドコンストラクタ
    public CartItemDto(Long id, String name, Integer price, Integer quantity, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imagePath = imagePath;
    }

    /**
     * CartItem Entity から CartItemDto に変換する静的ファクトリメソッド
     */
    public static CartItemDto fromEntity(CartItem entity) {
        if (entity == null || entity.getProduct() == null) {
            // エラーハンドリングまたはnullを返す
            return null; 
        }
        return new CartItemDto(
            entity.getId(),
            entity.getProduct().getName(),
            entity.getProduct().getPrice(), // ★ Productから price を取得
            entity.getQuantity(),
            entity.getProduct().getImagePath() // ★ Productから imagePath を取得
        );
    }
    
    // --- GetterとSetter --- (前回と同じため省略しますが、実装が必要です)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}