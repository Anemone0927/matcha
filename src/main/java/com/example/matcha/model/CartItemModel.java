package com.example.matcha.model;

import com.example.matcha.entity.CartItem; // Entityのインポート

/**
 * カートアイテムの表示用データ転送オブジェクト (DTO)。
 * View (Thymeleaf) や API レスポンスで使用されます。
 */
public class CartItemModel {

    // カートアイテム自体のID (削除時に使用)
    private Long id;

    // 商品ID (商品情報の識別に必要)
    private Long productId;

    // 商品名 (表示用)
    private String productName;

    // 数量
    private int quantity;

    // 単価 (計算に使用)
    private int price;

    // デフォルトコンストラクタ
    public CartItemModel() {}

    // 全フィールドコンストラクタ
    public CartItemModel(Long id, Long productId, String productName, int quantity, int price) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    /**
     * CartItem Entity から CartItemModel DTO を生成するファクトリメソッド。
     * @param entity CartItem エンティティ
     * @return CartItemModel DTO
     */
    public static CartItemModel fromEntity(CartItem entity) {
        if (entity == null) {
            return null;
        }

        CartItemModel model = new CartItemModel();
        model.setId(entity.getId());
        model.setQuantity(entity.getQuantity());

        // 商品情報が存在する場合のみ、その情報をDTOにマッピング
        if (entity.getProduct() != null) {
            model.setProductId(entity.getProduct().getId());
            model.setProductName(entity.getProduct().getName());
            model.setPrice(entity.getProduct().getPrice());
        } else {
            // 商品情報がない場合（エラーケースなど）のフォールバック
            model.setProductName("不明な商品");
            model.setPrice(0);
        }

        return model;
    }

    // --- Getter and Setter ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}