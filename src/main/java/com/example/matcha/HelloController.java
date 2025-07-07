package com.example.matcha;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("/hello")
    public String showHome() {
        return "hello";  // templates/hello.html を返す
    }

}
