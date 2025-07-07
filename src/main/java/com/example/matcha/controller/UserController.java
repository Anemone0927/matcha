package com.example.matcha.controller;

import com.example.matcha.entity.User;
import com.example.matcha.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

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

    // 簡単なログイン処理（JSONリクエスト・レスポンス想定）
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
}
