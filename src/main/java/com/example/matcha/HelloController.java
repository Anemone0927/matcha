package com.example.matcha;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class HelloController {

@GetMapping("/")
public String showIndex() {
    return "index";  // templates/index.html を返す
}


}
