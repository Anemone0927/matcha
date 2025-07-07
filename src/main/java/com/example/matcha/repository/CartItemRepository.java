package com.example.matcha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.matcha.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
