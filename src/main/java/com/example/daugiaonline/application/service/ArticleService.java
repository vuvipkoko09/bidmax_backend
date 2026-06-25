package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.ArticleRequest;
import com.example.daugiaonline.application.dto.ArticleResponse;
import com.example.daugiaonline.application.dto.ArticleReviewRequest;
import com.example.daugiaonline.application.dto.ArticleReviewResponse;
import com.example.daugiaonline.enums.ArticleStatus;

import java.util.List;

public interface ArticleService {
    ArticleResponse createArticle(ArticleRequest request);
    List<ArticleResponse> getAllArticles();
    List<ArticleResponse> getApprovedArticles();
    ArticleResponse getArticleById(Long id);
    ArticleResponse updateArticleStatus(Long id, ArticleStatus status);

    ArticleReviewResponse addReview(Long articleId, ArticleReviewRequest request);
    List<ArticleReviewResponse> getReviewsByArticle(Long articleId);
}
