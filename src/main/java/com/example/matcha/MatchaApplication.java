package com.example.matcha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.matcha.entity.Product;
import com.example.matcha.repository.ProductRepository;
import com.example.matcha.controller.ProductController;

@SpringBootApplication
public class MatchaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchaApplication.class, args);
    }

}
