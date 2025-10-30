package com.example.matcha.repository;

import com.example.matcha.entity.Post; // 💡 インポートを 'entity' パッケージからに変更
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 投稿データへのアクセスを担うリポジトリインターフェース
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // カスタムなクエリが必要な場合は、ここにメソッドを追加します
}
