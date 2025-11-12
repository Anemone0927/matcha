package com.example.matcha.service;

import com.example.matcha.model.CartItemModel;
import java.util.List;

/**
 * カート操作サービスインターフェース。
 * Controllerが依存する抽象化された操作を定義します。
 */
public interface CartService {

    /**
     * ユーザーIDに基づいてカートアイテムのリストを取得します。
     * @param userId 現在ログインしているユーザーのID
     * @return カートアイテムDTOのリスト
     */
    List<CartItemModel> getCartItemsByUserId(String userId);

    /**
     * カートに商品を追加、または数量を更新します。
     * @param userId 現在ログインしているユーザーのID
     * @param productId 追加する商品のID
     * @param quantity 追加する数量
     * @return 更新されたカートアイテムDTO
     */
    CartItemModel addItemToCart(String userId, Long productId, int quantity);

    /**
     * カートアイテムを削除します。
     * @param cartItemId 削除対象のカートアイテムID
     */
    void removeItemFromCart(Long cartItemId);
}