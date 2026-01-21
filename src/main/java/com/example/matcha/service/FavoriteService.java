package com.example.matcha.service;

import com.example.matcha.entity.Favorite;
import com.example.matcha.repository.FavoriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteService.class);

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Transactional
    public boolean addFavorite(Long userId, Long productId) {
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            return false;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favoriteRepository.save(favorite);
        return true;
    }

    @Transactional
    public void removeFavorite(Long userId, Long productId) {
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }
    
    public boolean isFavorited(Long userId, Long productId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }
    
    /**
     * ユーザーIDに紐づく全てのお気に入り商品IDを取得
     */
    public List<Long> getFavoriteProductIds(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        
        // デバッグ用ログ：ここが0件ならDBにデータがないか、UserIDが間違っています
        logger.info("ユーザーID: {} のお気に入りレコード数: {}", userId, favorites.size());
        
        return favorites.stream()
                .map(Favorite::getProductId)
                .collect(Collectors.toList());
    }
}