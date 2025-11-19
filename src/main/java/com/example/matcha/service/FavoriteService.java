package com.example.matcha.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.matcha.entity.Favorite;
import com.example.matcha.entity.Product;
import com.example.matcha.repository.FavoriteRepository;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductService productService; // 商品の存在チェックやエンティティ取得のためにProductServiceを利用

    // 定数: 今回はユーザー認証がないため、固定のユーザーIDを使用します。
    private static final Long DEFAULT_USER_ID = 1L; 

    // -------------------------------------------------------------------
    // 【★追加】お気に入り登録処理 (POST /api/products/{id}/favorite に対応)
    // -------------------------------------------------------------------
    @Transactional
    public void addFavorite(Long productId) {
        // 1. 商品エンティティを取得
        Optional<Product> productOpt = productService.findProductById(productId);
        if (productOpt.isEmpty()) {
            // 商品が存在しない場合は例外をスロー（ControllerがBadRequestを返す）
            throw new RuntimeException("Product with ID " + productId + " not found for adding favorite.");
        }
        Product product = productOpt.get();

        // 2. 既存のお気に入りを検索
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndProduct(DEFAULT_USER_ID, product);

        if (existingFavorite.isEmpty()) {
            // 3. 未登録の場合: 登録する
            Favorite newFavorite = new Favorite(DEFAULT_USER_ID, product);
            favoriteRepository.save(newFavorite);
        }
        // 登録済みの場合でも、特にエラーにはせず、処理を終える（冪等性）
    }
    
    // -------------------------------------------------------------------
    // 【★追加】お気に入り解除処理 (DELETE /api/products/{id}/favorite に対応)
    // -------------------------------------------------------------------
    @Transactional
    public void removeFavorite(Long productId) {
        // 1. 商品エンティティを取得（存在チェックのため）
        Optional<Product> productOpt = productService.findProductById(productId);
        if (productOpt.isEmpty()) {
            // 商品がない場合は削除対象がないだけなので、例外はスローせず、警告ログを出すだけに留めるのが一般的
            System.err.println("WARN: Product with ID " + productId + " not found for removing favorite. Skipping deletion.");
            return;
        }
        Product product = productOpt.get();

        // 2. 既存のお気に入りを検索
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndProduct(DEFAULT_USER_ID, product);

        if (existingFavorite.isPresent()) {
            // 3. 登録済みの場合: 解除（削除）する
            favoriteRepository.delete(existingFavorite.get());
        }
        // 未登録の場合でも、特にエラーにはせず、処理を終える（冪等性）
    }
    
    // -------------------------------------------------------------------
    // 既存のメソッド（そのまま残します）
    // -------------------------------------------------------------------

    /**
     * お気に入りの登録/解除（トグル）を行う（既存のメソッド）
     * @param productId 対象の商品ID
     * @return 処理後の状態 (true: 登録済み, false: 未登録)
     * @throws RuntimeException 商品が見つからない場合
     */
    @Transactional
    public boolean toggleFavorite(Long productId) {
        // 1. 商品エンティティを取得
        Optional<Product> productOpt = productService.findProductById(productId);
        if (productOpt.isEmpty()) {
            // 商品が存在しない場合はエラーをスロー
            throw new RuntimeException("Product with ID " + productId + " not found.");
        }
        Product product = productOpt.get();

        // 2. 既存のお気に入りを検索
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndProduct(DEFAULT_USER_ID, product);

        if (existingFavorite.isPresent()) {
            // 3. 登録済みの場合: 解除（削除）する
            favoriteRepository.delete(existingFavorite.get());
            return false; // 解除完了
        } else {
            // 4. 未登録の場合: 登録する
            Favorite newFavorite = new Favorite(DEFAULT_USER_ID, product);
            favoriteRepository.save(newFavorite);
            return true; // 登録完了
        }
    }

    /**
     * 現在のユーザーのお気に入り商品IDのセットを取得します。
     */
    public Set<Long> getFavoriteProductIdsForCurrentUser() {
        // DEFAULT_USER_ID を使ってユーザーのお気に入り情報を取得
        List<Favorite> favorites = favoriteRepository.findByUserId(DEFAULT_USER_ID);
        
        // お気に入りリストから商品IDのみを抽出し、Setにして返却
        return favorites.stream()
                .map(favorite -> favorite.getProduct().getId())
                .collect(Collectors.toSet());
    }

    /**
     * 指定された商品が現在のお気に入りリストに含まれているかチェックする。
     */
    public boolean isFavorite(Long productId) {
        Optional<Product> productOpt = productService.findProductById(productId);
        if (productOpt.isEmpty()) {
            return false; // 商品がない場合はお気に入りではない
        }
        Product product = productOpt.get();
        // RepositoryのfindByUserIdAndProductを使用
        return favoriteRepository.findByUserIdAndProduct(DEFAULT_USER_ID, product).isPresent();
    }
}