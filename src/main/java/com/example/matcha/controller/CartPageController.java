package com.example.matcha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartPageController {

    @GetMapping("/cart/add")
    public String showAddCartForm() {
        return "add_cart";  // add_cart.html を表示する
    }
}
