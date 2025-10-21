package com.example.matcha.controller;

import com.example.matcha.entity.User;
import com.example.matcha.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils; // パスワードの空チェックに使用

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 💡 認証選択ページ（auth_select.html）表示を追加
    @GetMapping("/auth_select")
    public String showAuthSelectPage() {
        return "auth_select"; // auth_select.htmlを表示
    }

    // ユーザー登録フォーム表示
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());  // 空のUserオブジェクトを渡す
        return "user_register";  // user_register.htmlを表示（共通レイアウトなし）
    }

    // ユーザー登録処理（フォームからPOST）
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userRepository.save(user);
        return "redirect:/users/register?success";  // 登録後、同ページにリダイレクト
    }

    // ログインフォーム表示
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        // Thymeleafでエラーメッセージなどを表示できるように、空のUserオブジェクトを渡しておきます。
        model.addAttribute("user", new User());
        return "login"; // login.htmlを表示
    }

    // 既存の簡単なログイン処理（JSONリクエスト・レスポンス想定）
    @PostMapping("/login")
    @ResponseBody
    public String loginUser(@RequestBody User loginUser) {
        Optional<User> userOpt = userRepository.findByEmail(loginUser.getEmail());

        if (userOpt.isEmpty()) {
            return "エラー：メールアドレスが見つかりません";
        }

        User user = userOpt.get();

        // パスワードの簡単な比較（ハッシュ化省略）
        if (user.getPassword().equals(loginUser.getPassword())) {
            return "ログイン成功: " + user.getName();
        } else {
            return "エラー：パスワードが違います";
        }
    }
    
    // 💡 プロフィール表示（ユーザー情報編集）
    @GetMapping("/profile")
    public String showProfile(Model model) {
        // 🚨 注意: 本来は認証情報から現在のユーザーIDを取得しますが、ここでは仮にID=1のユーザーを取得します。
        Optional<User> userOpt = userRepository.findById(1L);

        if (userOpt.isPresent()) {
            // パスワードをフォームに表示しないため、念のためここでnull化しておきます。（重要）
            User user = userOpt.get();
            user.setPassword(null);
            model.addAttribute("user", user);
        } else {
            // 仮のデータを作成し、編集画面を表示できるようにする
            User dummyUser = new User();
            dummyUser.setId(0L); // IDを仮に設定
            dummyUser.setName("");
            dummyUser.setEmail("");
            model.addAttribute("user", dummyUser);
            // 本番環境では、ログインページにリダイレクトするなどの処理が必要です。
        }

        return "profile"; // profile.htmlを表示
    }

    // 💡 プロフィール更新処理 (パスワード維持ロジックを追加)
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User updatedUser) {
        // 1. 更新対象のユーザーID（ここでは仮に1L）を使用して、DBから現在の情報を取得
        Optional<User> existingUserOpt = userRepository.findById(1L); // 🚨 実際のアプリケーションでは、認証情報からユーザーIDを取得してください

        if (existingUserOpt.isEmpty()) {
            // ユーザーが存在しない場合はエラーまたはログインページにリダイレクト
            return "redirect:/users/login?error"; 
        }

        User existingUser = existingUserOpt.get();

        // 2. フォームから新しいパスワードが入力されているかチェック
        // StringUtils.hasText() は、null、空文字列、空白のみの文字列をチェックできます。
        if (StringUtils.hasText(updatedUser.getPassword())) {
            // パスワードが入力されている場合、新しいパスワードを設定 (本来はハッシュ化が必要)
            existingUser.setPassword(updatedUser.getPassword());
        } 
        // パスワードが入力されていない場合 (else)、existingUserのパスワードはそのまま維持されます。（← これが重要）

        // 3. フォームから渡された他のフィールド（名前、メール）を既存のユーザー情報にコピーして上書き
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());

        // 4. 更新されたUserオブジェクトを保存
        userRepository.save(existingUser);
        
        return "redirect:/users/profile?updated"; // 更新後、プロフィールページにリダイレクト
    }
}
