package com.example.matcha.service;

import com.example.matcha.model.CartItemModel;
import java.util.List;

/**
 * カート機能のビジネスロジックを定義するインターフェース。
 * Controllerがこのインターフェースを通じて機能を利用します。
 */
public interface CartService {

    /**
     * 現在のユーザーのカートアイテムをすべて取得し、DTOとして返します。
     * @return CartItemModelのリスト
     */
    List<CartItemModel> findAllItems();

    /**
     * 指定されたIDのカートアイテムを削除します。
     * @param itemId 削除するアイテムのID
     */
    void deleteItem(Long itemId);
    
    // 💡 既存のロジックを保持するため、商品追加メソッドも定義
    void addItemToCart(String userId, Long productId, int quantity);
}