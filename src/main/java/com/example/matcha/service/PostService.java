package com.example.matcha.service;

import com.example.matcha.entity.Post;
import com.example.matcha.repository.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.List;

/**
 * 投稿に関する業務ロジックを処理するサービス
 */
@Service
public class PostService {

    private final PostRepository postRepository;

    // application.propertiesからファイルのアップロードディレクトリを取得
    @Value("${upload.dir:./uploads}")
    private String uploadDir;

    // コンストラクタによる依存性の注入
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // --- CRUD operations ---

    /**
     * 新しい投稿を保存し、画像ファイルをディスクに保存します。
     * @param caption 投稿の説明文
     * @param imageFile アップロードされた画像ファイル
     * @return 保存されたPostエンティティ
     */
    @Transactional
    public Post savePost(String caption, MultipartFile imageFile) throws IOException { // 🚨 PostControllerが要求するメソッドシグネチャ
        
        // 1. 画像ファイルをディスクに保存
        String fileName = saveImageFile(imageFile);

        // 2. DBエンティティを作成し、保存
        Post post = new Post();
        post.setCaption(caption);
        post.setImageFileName(fileName); // ファイル名をDBに保存
        
        return postRepository.save(post);
    }
    
    /**
     * 投稿IDに基づいて、データベースのレコードと関連する物理画像ファイルを削除します。
     * @param id 削除対象のPost ID
     */
    @Transactional
    public void deletePost(Long id) {
        // 1. DBからPostオブジェクトを取得
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post (ID: " + id + ") not found."));

        // 2. 物理ファイルを削除する
        if (post.getImageFileName() != null && !post.getImageFileName().isEmpty()) {
            deletePhysicalFile(post.getImageFileName());
        }

        // 3. データベースから投稿レコードを削除
        postRepository.delete(post);
        System.out.println("✅ Post record deleted successfully from DB.");
    }

    /**
     * 全ての投稿を取得します。
     * @return 全てのPostのリスト
     */
    public List<Post> findAllPosts() { // 🚨 PostControllerが要求するメソッドシグネチャ
        return postRepository.findAll();
    }

    // --- Helper methods for file operations ---

    /**
     * 画像ファイルをディスクに保存し、生成されたファイル名を返します。
     */
    private String saveImageFile(MultipartFile file) throws IOException {
        // ディレクトリが存在しない場合は作成
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // ファイル名をユニークにする (UUIDを使用)
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName != null && originalFileName.contains(".") ? 
                               originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        Path filePath = uploadPath.resolve(uniqueFileName);
        
        // ファイルを保存
        Files.copy(file.getInputStream(), filePath);

        return uniqueFileName;
    }

    /**
     * 指定されたファイル名の物理ファイルを削除します。
     */
    private void deletePhysicalFile(String fileName) {
        Path fileToDelete = Paths.get(uploadDir, fileName);

        try {
            if (Files.exists(fileToDelete)) {
                Files.delete(fileToDelete);
                System.out.println("✅ File deleted successfully: " + fileToDelete.toString());
            } else {
                System.out.println("⚠️ File not found on disk, skipping physical deletion: " + fileToDelete.toString());
            }
        } catch (IOException e) {
            System.err.println("❌ Could not delete file: " + fileToDelete.toString() + ". Error: " + e.getMessage());
            // 物理削除エラーはDBトランザクションをロールバックさせないように、ここでは例外を再スローしません
        }
    }
}
