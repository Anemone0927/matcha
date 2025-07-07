package com.example.matcha.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.multipart.MultipartFile;

import com.example.matcha.entity.Product;
import com.example.matcha.repository.ProductRepository;

@Controller
public class ProductController {

    @GetMapping("/products")
    @ResponseBody
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Value("${upload.dir}")
    private String uploadDir;

    @Autowired
    private ProductRepository productRepository;

    // 商品一覧画面表示（Thymeleafでrender）
    @GetMapping("/products_list")
    public String showList(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "products_list";  // templates/products_list.htmlを直接返す
    }

    // 商品追加フォーム画面表示
    @GetMapping("/products/new")
    public String showForm(Model model) {
        model.addAttribute("product", new Product());
        return "products_form";  // templates/products_form.htmlを直接返す
    }

    // 商品追加処理（画像アップロード含む）
    @PostMapping(value = "/products/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String addProduct(
        @RequestParam String name,
        @RequestParam int price,
        @RequestParam MultipartFile image,
        Model model) {

        String filename = saveImage(image);

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setImagePath("/images/" + filename);

        productRepository.save(product);

        return "redirect:/products_list";  // 追加後は一覧画面へリダイレクト
    }

    // 画像保存メソッド
    private String saveImage(MultipartFile imageFile) {
        try {
            String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path path = Paths.get(uploadDir, filename);
            Files.createDirectories(path.getParent());
            Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("画像の保存に失敗しました", e);
        }
    }

    // 商品削除（API的に）
    @DeleteMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.ok("削除しました！");
    }

    // 商品詳細取得（API的にJSONで返す）
    @GetMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
@GetMapping("/products/edit/{id}")
public String editProduct(@PathVariable Long id, Model model) {
    Optional<Product> product = productRepository.findById(id);
    if (product.isPresent()) {
        model.addAttribute("product", product.get());
        return "products_edit";  // templates/products_edit.html
    } else {
        return "error/404"; // 存在しない商品だった場合
    }
}
@PostMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public String updateProduct(
    @PathVariable Long id,
    @RequestParam("name") String name,
    @RequestParam("price") int price,
    @RequestParam(value = "image", required = false) MultipartFile image,
    Model model) {

    Optional<Product> optProduct = productRepository.findById(id);
    if (optProduct.isPresent()) {
        Product product = optProduct.get();
        product.setName(name);
        product.setPrice(price);

        if (image != null && !image.isEmpty()) {
            String filename = saveImage(image);
            product.setImagePath("/images/" + filename);
        }

        productRepository.save(product);
    }

    return "redirect:/products_list"; // 更新後は一覧へ戻る
}

}
