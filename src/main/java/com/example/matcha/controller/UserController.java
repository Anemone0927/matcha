package com.example.matcha.controller;

import com.example.matcha.entity.User;
import com.example.matcha.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 
import jakarta.servlet.http.HttpSession; 

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {
    
    // ログイン中のユーザーIDを保存するためのセッションキー
    private static final String SESSION_USER_ID_KEY = "loggedInUserId";

    @Autowired
    private UserRepository userRepository;

    /**
     * ログインしているユーザーのIDをセッションから取得する共通メソッド
     */
    private Long getLoggedInUserId(HttpSession session) {
        return (Long) session.getAttribute(SESSION_USER_ID_KEY);
    }
    
    /* -----------------------------------------------------
     * マイページ表示
     * ----------------------------------------------------- */

    /**
     * マイページを表示する
     */
    @GetMapping("/mypage")
    public String myPage(Model model, HttpSession session) {
        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            return "redirect:/users/login"; 
        }

        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(null); 
            model.addAttribute("user", user);
        } else {
            session.removeAttribute(SESSION_USER_ID_KEY);
            return "redirect:/users/login?error=notfound";
        }
        
        return "mypage"; 
    }

    /* -----------------------------------------------------
     * 認証（登録・ログイン）関連
     * ----------------------------------------------------- */

    /**
     * 認証選択ページ表示
     */
    @GetMapping("/auth_select")
    public String showAuthSelectPage() {
        return "auth_select";
    }

    /**
     * ユーザー登録フォーム表示
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "user_register";
    }

    /**
     * ユーザー登録処理（フォームからPOST）
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, 
                               RedirectAttributes redirectAttributes,
                               HttpSession session) { 
        
        // メールアドレスの重複チェック
        if (userRepository.existsByEmail(user.getEmail())) {
            user.setPassword(null);
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("errorMessage", "このメールアドレスは既に使用されています。");
            return "redirect:/users/register";
        }

        // 🚨 注意: 実際にはここでパスワードのハッシュ化（BCryptなど）が必要です。
        User savedUser = userRepository.save(user);
        
        // 登録成功後、セッションにユーザーIDを格納
        session.setAttribute(SESSION_USER_ID_KEY, savedUser.getId());
        
        // マイページにリダイレクト
        return "redirect:/users/mypage";
    }

    /**
     * ログインフォーム表示
     */
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    /**
     * ログイン処理（HTMLフォームからPOST）
     * 💡 修正点: @RequestBodyから@ModelAttributeに変更し、リダイレクトベースの処理にする
     */
    @PostMapping("/login")
    public String loginUser(@ModelAttribute User loginUser, 
                            HttpSession session,
                            RedirectAttributes redirectAttributes) { 
        
        Optional<User> userOpt = userRepository.findByEmail(loginUser.getEmail());

        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "メールアドレスが見つかりません。");
            return "redirect:/users/login";
        }

        User user = userOpt.get();

        // 🚨 注意: 実際にはここでパスワードのハッシュ化と比較が必要です。
        if (user.getPassword().equals(loginUser.getPassword())) {
            // ログイン成功後、セッションにユーザーIDを格納し、マイページへリダイレクト
            session.setAttribute(SESSION_USER_ID_KEY, user.getId());
            return "redirect:/users/mypage";
        } else {
            // パスワードエラー
            redirectAttributes.addFlashAttribute("errorMessage", "パスワードが違います。");
            return "redirect:/users/login";
        }
    }
    
    /* -----------------------------------------------------
     * プロフィール編集関連
     * ----------------------------------------------------- */

    /**
     * プロフィール編集フォーム表示
     */
    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) { 
        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            return "redirect:/users/login"; 
        }

        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(null);
            model.addAttribute("user", user);
        } else {
            session.removeAttribute(SESSION_USER_ID_KEY);
            return "redirect:/users/login?error=notfound";
        }

        return "profile";
    }

    /**
     * プロフィール更新処理
     */
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User updatedUser, HttpSession session) { 
        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            return "redirect:/users/login?error=unauthorized";
        }

        Optional<User> existingUserOpt = userRepository.findById(userId);

        if (existingUserOpt.isEmpty()) {
            session.removeAttribute(SESSION_USER_ID_KEY);
            return "redirect:/users/login?error";
        }

        User existingUser = existingUserOpt.get();

        if (!existingUser.getId().equals(updatedUser.getId())) {
             return "redirect:/users/profile?error=security"; 
        }

        // パスワード更新処理 (入力があった場合のみ)
        if (StringUtils.hasText(updatedUser.getPassword())) {
            // 🚨 注意: 実際にはここでパスワードのハッシュ化が必要です。
            existingUser.setPassword(updatedUser.getPassword());
        }

        // 名前とメールアドレスの更新
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());

        userRepository.save(existingUser);
        
        return "redirect:/users/profile?updated";
    }
}