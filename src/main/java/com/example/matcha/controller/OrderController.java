// com.example.matcha.controller.OrderController.java
package com.example.matcha.controller;

import org.springframework.stereotype.Controller;

import java.util.List;


import com.example.matcha.entity.Order;
import com.example.matcha.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import org.springframework.ui.Model;
import java.util.Optional;

import com.example.matcha.entity.Product;
import com.example.matcha.repository.ProductRepository;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/list")
    public String showOrderList(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "order_list"; // ← order_list.html を用意
    }
    @PostMapping("")
    public String createOrder(@ModelAttribute Order order) {
        order.setOrderDate(LocalDateTime.now());
        orderRepository.save(order);
        return "order_success";  // 注文完了ページへリダイレクト
    }
    @GetMapping("/form")
    public String showOrderForm(Model model) {
        model.addAttribute("order", new Order()); // ← 空の注文オブジェクト
        model.addAttribute("products", productRepository.findAll()); // 商品リストを渡す
        return "order_form";
    }



}
