package com.example.matcha.controller;

import com.example.matcha.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * お気に入り状態を切り替える (登録/解除)
     * POST /favorite/toggle/{productId} で呼び出されます。
     * @param productId 対象の商品ID
     * @return 成功ステータス (200 OK) と、処理後の状態 (JSON boolean)
     */
    @PostMapping("/toggle/{productId}")
    @ResponseBody
    public ResponseEntity<?> toggleFavorite(@PathVariable Long productId) {
        try {
            // FavoriteServiceで登録/解除のロジックを実行
            boolean isNowFavorite = favoriteService.toggleFavorite(productId);

            // 成功時は200 OKと、新しい状態を返す
            return ResponseEntity.ok().body(isNowFavorite);

        } catch (RuntimeException e) {
            // 商品が見つからない、DBエラーなどの例外が発生した場合
            System.err.println("Error toggling favorite for product ID " + productId + ": " + e.getMessage());

            // 画面に「お気に入り登録中にエラーが発生しました。」と表示されるように500エラーを返す
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("お気に入り処理中にエラーが発生しました。");
        }
    }
}