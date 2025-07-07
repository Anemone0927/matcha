package com.example.matcha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.matcha.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // JpaRepositoryが基本CRUDメソッドを提供するよ
}
