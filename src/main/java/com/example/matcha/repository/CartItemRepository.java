package com.example.matcha.repository;



import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;



import com.example.matcha.entity.CartItem;



@Repository

public interface CartItemRepository extends JpaRepository<CartItem, Long> {



    // 💡 修正点 1: ユーザーIDに基づいてカートアイテムのリストを取得するメソッドを追加

    List<CartItem> findByUserId(String userId);
    Optional<CartItem> findByUserIdAndProductId(String userId, Long productId);
}