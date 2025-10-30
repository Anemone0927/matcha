package com.example.matcha.controller;

import com.example.matcha.entity.Post; // 💡 インポートを 'entity' パッケージからに変更
import com.example.matcha.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.List;

/**
 * 投稿に関するHTTPリクエストを処理するコントローラー
 */
@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    // PostServiceをコンストラクタで注入
    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 投稿一覧ページを表示します。
     */
    @GetMapping
    public String listPosts(Model model) {
        List<Post> posts = postService.findAllPosts();
        model.addAttribute("posts", posts);
        return "posts/list"; // 💡 posts/list.html へのマッピングを想定
    }

    /**
     * 新規投稿フォームを表示します。
     */
    @GetMapping("/new")
    public String newPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "posts/form"; // 💡 posts/form.html へのマッピングを想定
    }

    /**
     * 新しい投稿を処理します (画像アップロードとDB保存)。
     */
    @PostMapping
    public String createPost(
            @RequestParam("caption") String caption,
            @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            if (imageFile.isEmpty()) {
                // ファイルがない場合の処理（必要に応じてエラーメッセージを追加）
                return "redirect:/posts/new"; 
            }
            postService.savePost(caption, imageFile);
            return "redirect:/posts"; // 投稿一覧へリダイレクト
        } catch (IOException e) {
            System.err.println("Error saving post and file: " + e.getMessage());
            // エラー処理（ユーザーにフィードバックを返すなど）
            return "redirect:/posts/new?error=saveFailed";
        }
    }

    /**
     * 投稿と関連する物理ファイルの両方を削除します。
     * 削除後は投稿一覧ページへリダイレクトします。
     */
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return "redirect:/posts"; // 削除成功後、一覧へリダイレクト
        } catch (RuntimeException e) {
            // Postが見つからなかった場合などのエラー処理
            System.err.println("Error deleting post: " + e.getMessage());
            // エラーを付けてリダイレクトし、一覧ページでメッセージを表示できるようにする
            return "redirect:/posts?error=deleteFailed";
        }
    }
}
