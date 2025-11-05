package com.example.matcha.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.matcha.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // ユーザーIDに基づいてカートアイテムのリストを取得する
    List<CartItem> findByUserId(String userId);
}