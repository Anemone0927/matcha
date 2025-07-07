package com.example.matcha.controller;

import com.example.matcha.entity.Review;
import com.example.matcha.repository.ReviewRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;



@Controller
public class ReviewPageController {

    @Autowired
    private ReviewRepository reviewRepository;


    @GetMapping("/review/form")
    public String showReviewForm() {
        return "review_form";
    }

    @GetMapping("/reviews/list")
    public String showReviewList(Model model) {
        List<Review> reviews = reviewRepository.findAll();
        model.addAttribute("reviews", reviews);
        return "review_list";
    }



}
