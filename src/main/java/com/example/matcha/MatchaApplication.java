package com.example.matcha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.context.annotation.Bean; // 削除

// import com.example.matcha.entity.Product; // 削除
// import com.example.matcha.repository.ProductRepository; // 削除
// import com.example.matcha.controller.ProductController; // 削除 (通常、メインクラスでControllerはインポートしないため)

@SpringBootApplication
public class MatchaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchaApplication.class, args);
    }
    
    // 💡 備考:
    // もし以前、このクラスに以下のような初期化Beanがあった場合は、完全に削除する必要があります。
    // @Bean
    // public CommandLineRunner initData(ProductRepository productRepository) {
    //     return (args) -> {
    //         // productRepository.save(new Product(...));
    //     };
    // }
}
