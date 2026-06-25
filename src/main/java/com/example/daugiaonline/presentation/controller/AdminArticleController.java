package com.example.daugiaonline.presentation.controller;

import com.example.daugiaonline.application.dto.ArticleResponse;
import com.example.daugiaonline.application.service.ArticleService;
import com.example.daugiaonline.enums.ArticleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/articles")
@RequiredArgsConstructor
public class AdminArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ArticleResponse> updateArticleStatus(@PathVariable Long id, @RequestParam ArticleStatus status) {
        return ResponseEntity.ok(articleService.updateArticleStatus(id, status));
    }
}
