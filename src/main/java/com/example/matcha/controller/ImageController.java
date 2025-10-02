package com.example.matcha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ImageController {

    @GetMapping("/top.jpg")
    @ResponseBody
    public Resource getTopImage() {
        Path path = Paths.get("C:/Users/24i1004/matcha/src/main/resources/static/images/top.jpg");
        return new FileSystemResource(path.toFile());
    }
}
