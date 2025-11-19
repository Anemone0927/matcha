package com.example.matcha.controller;

import com.example.matcha.service.FavoriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; // @RestControllerを使用

/**
 * お気に入り機能に関するAPIを扱うコントローラー。
 * パスを /api/products に合わせ、POST/DELETEで登録・解除を分離。
 */
@RestController 
@RequestMapping("/api/products") // 【★重要修正】ベースURLをフロントエンドに合わせる
public class FavoriteController {
    
    private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);

    @Autowired
    private FavoriteService favoriteService;

    /**
     * POST /api/products/{productId}/favorite
     * お気に入り登録処理
     */
    @PostMapping("/{productId}/favorite") // 【★修正】
    public ResponseEntity<String> addFavorite(@PathVariable Long productId) {
        logger.info("お気に入り登録リクエスト: 商品ID {}", productId);
        
        try {
            // FavoriteServiceに登録ロジックを委譲 (要実装)
            // 例: favoriteService.addFavorite(productId); 
            
            // 成功を返す
            return ResponseEntity.ok().body("お気に入り登録が完了しました。");
        } catch (Exception e) {
            logger.error("お気に入り登録中にエラーが発生しました。商品ID: {}", productId, e);
            // ログインエラーなど、具体的なエラーハンドリングを行う
            return ResponseEntity.badRequest().body("登録処理中に失敗しました。");
        }
    }

    /**
     * DELETE /api/products/{productId}/favorite
     * お気に入り解除処理
     */
    @DeleteMapping("/{productId}/favorite") // 【★修正】
    public ResponseEntity<?> removeFavorite(@PathVariable Long productId) {
        logger.info("お気に入り解除リクエスト: 商品ID {}", productId);

        try {
            // FavoriteServiceに解除ロジックを委譲 (要実装)
            // 例: favoriteService.removeFavorite(productId);
            
            // 204 No Content (コンテンツなしの成功) を返す
            return ResponseEntity.noContent().build(); 
        } catch (Exception e) {
            logger.error("お気に入り解除中にエラーが発生しました。商品ID: {}", productId, e);
            return ResponseEntity.badRequest().body("解除処理中に失敗しました。");
        }
    }
}