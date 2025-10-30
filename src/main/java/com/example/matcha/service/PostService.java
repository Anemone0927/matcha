package com.example.matcha.service;

import com.example.matcha.model.Post; // 💡 あなたのPostモデルに合わせてパッケージ名を修正してください
import com.example.matcha.repository.PostRepository; // 💡 あなたのPostRepositoryに合わせてパッケージ名を修正してください
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// @Serviceアノテーションは、このクラスがサービス層のコンポーネントであることを示します
@Service
public class PostService {

    // PostRepositoryを注入（データベース操作に必要）
    private final PostRepository postRepository;

    // application.propertiesからファイルのアップロードディレクトリを取得
    @Value("${upload.dir:./uploads}") // :./uploads はデフォルト値
    private String uploadDir;

    // コンストラクタによる依存性の注入
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * 投稿IDに基づいて、データベースのレコードと関連する物理画像ファイルを削除します。
     * * @param id 削除対象のPost ID
     */
    @Transactional // データベース操作をトランザクション管理下に置く
    public void deletePost(Long id) {
        // 1. DBからPostオブジェクトを取得
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post (ID: " + id + ") not found."));

        // 2. 物理ファイルを削除する
        //    ※ Postモデルに画像ファイル名を取得する getImageFileName() メソッドがあると仮定
        if (post.getImageFileName() != null && !post.getImageFileName().isEmpty()) {
            
            // ファイルのフルパスを構成 (例: "./uploads/abc-123.jpg")
            Path fileToDelete = Paths.get(uploadDir, post.getImageFileName());
            
            try {
                if (Files.exists(fileToDelete)) {
                    // 実際にファイルを削除する
                    Files.delete(fileToDelete);
                    System.out.println("✅ File deleted successfully: " + fileToDelete.toString());
                } else {
                    System.out.println("⚠️ File not found on disk, skipping physical deletion: " + fileToDelete.toString());
                }
            } catch (IOException e) {
                // ファイルの削除に失敗した場合、エラーを出力してログに残す
                System.err.println("❌ Could not delete file: " + fileToDelete.toString() + ". Error: " + e.getMessage());
                // ファイルが削除できなくてもDBのレコードは削除するため、ここでは例外を再スローしない
            }
        }

        // 3. データベースから投稿レコードを削除
        postRepository.delete(post);
        System.out.println("✅ Post record deleted successfully from DB.");
    }
    
    // 他にも、findById や findAll などのメソッドをここに追加できます。
    // 例:
    // public List<Post> findAll() { return postRepository.findAll(); }
    // public Post findById(Long id) { return postRepository.findById(id).orElse(null); }
}
