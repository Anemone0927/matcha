package com.example.matcha.service;

import com.example.matcha.dto.CartItemDto; // DTOを修正
import java.util.List;

/**
 * カート操作サービスインターフェース。
 */
public interface CartService {

    /**
     * ユーザーIDに基づいてカートアイテムのDTOリストを取得します。
     * @param userId 現在ログインしているユーザーのID
     * @return カートアイテムDTOのリスト
     */
    List<CartItemDto> getCartItemsForCurrentUser(String userId); // ★メソッド名を修正

    /**
     * カートに商品を追加、または数量を更新します。
     * @param userId 現在ログインしているユーザーのID
     * @param productId 追加する商品のID
     * @param quantity 追加する数量
     * @return 更新されたカートアイテムDTO
     */
    CartItemDto addItemToCart(String userId, Long productId, int quantity);

    /**
     * カートアイテムの数量を更新します。
     * @param cartItemId 数量更新対象のカートアイテムID
     * @param newQuantity 新しい数量 (1以上)
     */
    void updateQuantity(Long cartItemId, Integer newQuantity); // ★追加

    /**
     * カートアイテムを削除します。
     * @param cartItemId 削除対象のカートアイテムID
     */
    void removeItemFromCart(Long cartItemId);
}