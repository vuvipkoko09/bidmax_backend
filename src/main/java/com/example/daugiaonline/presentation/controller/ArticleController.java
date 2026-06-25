package com.example.daugiaonline.presentation.controller;

import com.example.daugiaonline.application.dto.ArticleRequest;
import com.example.daugiaonline.application.dto.ArticleResponse;
import com.example.daugiaonline.application.dto.ArticleReviewRequest;
import com.example.daugiaonline.application.dto.ArticleReviewResponse;
import com.example.daugiaonline.application.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getApprovedArticles() {
        return ResponseEntity.ok(articleService.getApprovedArticles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(@Valid @RequestBody ArticleRequest request) {
        return ResponseEntity.ok(articleService.createArticle(request));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ArticleReviewResponse>> getReviewsByArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getReviewsByArticle(id));
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<ArticleReviewResponse> addReview(@PathVariable Long id, @Valid @RequestBody ArticleReviewRequest request) {
        return ResponseEntity.ok(articleService.addReview(id, request));
    }
}
