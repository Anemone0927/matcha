package com.example.matcha.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")  // データベース上のテーブル名
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自動でID採番
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)  // メールアドレスはユニーク
    private String email;

    @Column(nullable = false)
    private String password;

    // --- デフォルトコンストラクタ（必須） ---
    public User() {
    }

    // --- フルコンストラクタ（オプション） ---
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // --- Getter・Setter ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
