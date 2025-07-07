// com.example.matcha.repository.OrderRepository.java
package com.example.matcha.repository;

import com.example.matcha.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
