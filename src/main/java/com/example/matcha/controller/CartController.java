package com.example.matcha.controller;

import com.example.matcha.dto.CartItemDto;
import com.example.matcha.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * カート機能のAPI操作を管理するコントローラー
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;
    private static final String DEFAULT_USER_ID = "1"; // 仮のユーザーID

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    
    /**
     * GET /api/cart
     * カートの内容を全て取得します。
     * 405 Method Not Allowed エラーを解消するためにこのメソッドを追加します。
     */
    @GetMapping
    public ResponseEntity<List<CartItemDto>> getCartItems() {
        logger.info("カートアイテム一覧取得リクエスト");
        try {
            // Service経由でカートの内容を取得
            List<CartItemDto> items = cartService.getCartItemsForCurrentUser(DEFAULT_USER_ID);
            // 成功時は 200 OK と共にアイテムリストを返却
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            logger.error("カートアイテム一覧取得中に予期せぬエラーが発生しました。", e);
            // 予期せぬサーバーエラーは 500 Internal Server Error を返す
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/cart
     * カートに商品を追加します
     */
    @PostMapping
    public ResponseEntity<CartItemDto> addItem(@RequestBody Map<String, Object> itemRequest) {
        
        Long productId;
        Integer quantity;
        
        try {
            // リクエストボディの型チェックと取得
            productId = ((Number) itemRequest.get("productId")).longValue();
            quantity = (Integer) itemRequest.getOrDefault("quantity", 1);
        } catch (Exception e) {
            logger.error("リクエストボディのパースに失敗しました。", e);
            // 形式不正 -> 400 Bad Request (ボディなし)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        logger.info("カートに追加リクエスト: Product ID {}, Quantity {}", productId, quantity);
        
        try {
            CartItemDto savedItem = cartService.addItemToCart(DEFAULT_USER_ID, productId, quantity);
            return ResponseEntity.ok(savedItem);
            
        } catch (IllegalArgumentException e) {
            // Service層でスローされたデータ不足/商品ID不備などのエラー -> 400 Bad Request (ボディなし)
            logger.error("カート追加時のデータエラー: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); 
            
        } catch (Exception e) {
            // その他の予期せぬサーバーエラー -> 500 Internal Server Error (ボディなし)
            logger.error("カート追加中に予期せぬサーバーエラーが発生しました。", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
        }
    }

    /**
     * PUT /api/cart/{cartItemId}
     * カートアイテムの数量を更新
     */
    @PutMapping("/{cartItemId}")
    public ResponseEntity<Void> updateCartItemQuantity(@PathVariable Long cartItemId, @RequestBody Map<String, Integer> requestBody) {
        Integer newQuantity = requestBody.get("quantity");

        if (newQuantity == null || newQuantity <= 0) {
            return ResponseEntity.badRequest().build();
        }

        try {
            cartService.updateQuantity(cartItemId, newQuantity);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            // CartItemが見つからない場合は404 Not Found
            logger.error("カートアイテム更新中にデータエラー: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("数量更新中に予期せぬエラーが発生しました。CartItem ID: {}", cartItemId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * DELETE /api/cart/{cartItemId}
     * カートから商品を削除します
     */
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<String> removeCartItem(@PathVariable Long cartItemId) {
        try {
            cartService.removeItemFromCart(cartItemId);
            // 削除成功時は 200 OK とメッセージを返す
            return ResponseEntity.ok().body("アイテムをカートから取り出しました。");
        } catch (Exception e) {
            logger.error("カートアイテム削除中にエラーが発生しました。CartItem ID: {}", cartItemId, e);
            // 削除失敗時は 500 Internal Server Error とメッセージを返す
            return ResponseEntity.internalServerError().body("削除処理中に失敗しました。");
        }
    }
}