package com.example.matcha.repository;

import com.example.matcha.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // エラーメッセージに出ていた2つのメソッドを追加！
    boolean existsByEmail(String email);


}
