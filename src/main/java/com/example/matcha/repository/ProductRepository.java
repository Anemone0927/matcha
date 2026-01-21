package com.example.matcha.repository;

import com.example.matcha.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ProductエンティティのCRUD操作とカスタムクエリを提供するリポジトリ。
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // JpaRepositoryを拡張しているため、
    // findAllById(Iterable<ID> ids) メソッドが自動的に提供されます。
    // FavoriteControllerが要求している findProductsByIds の実体はこのメソッドを使用します。

    // 例外的なカスタムメソッドが必要な場合に記述
    // List<Product> findByPriceLessThan(Integer price);
}