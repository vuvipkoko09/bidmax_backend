package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.dto.ArticleRequest;
import com.example.daugiaonline.application.dto.ArticleResponse;
import com.example.daugiaonline.application.dto.ArticleReviewRequest;
import com.example.daugiaonline.application.dto.ArticleReviewResponse;
import com.example.daugiaonline.application.service.ArticleService;
import com.example.daugiaonline.entity.Article;
import com.example.daugiaonline.entity.ArticleReview;
import com.example.daugiaonline.entity.User;
import com.example.daugiaonline.enums.ArticleStatus;
import com.example.daugiaonline.exception.ResourceNotFoundException;
import com.example.daugiaonline.infrastructure.repository.ArticleRepository;
import com.example.daugiaonline.infrastructure.repository.ArticleReviewRepository;
import com.example.daugiaonline.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleReviewRepository articleReviewRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ArticleResponse createArticle(ArticleRequest request) {
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getAuthorId()));

        Article article = Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .thumbnail(request.getThumbnail())
                .author(author)
                .status(ArticleStatus.PENDING)
                .build();

        return mapToResponse(articleRepository.save(article));
    }

    @Override
    public List<ArticleResponse> getAllArticles() {
        return articleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleResponse> getApprovedArticles() {
        return articleRepository.findByStatusOrderByCreatedAtDesc(ArticleStatus.APPROVED).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ArticleResponse getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
        return mapToResponse(article);
    }

    @Override
    @Transactional
    public ArticleResponse updateArticleStatus(Long id, ArticleStatus status) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
        article.setStatus(status);
        return mapToResponse(articleRepository.save(article));
    }

    @Override
    @Transactional
    public ArticleReviewResponse addReview(Long articleId, ArticleReviewRequest request) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        ArticleReview review = ArticleReview.builder()
                .article(article)
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return mapToReviewResponse(articleReviewRepository.save(review));
    }

    @Override
    public List<ArticleReviewResponse> getReviewsByArticle(Long articleId) {
        return articleReviewRepository.findByArticleIdOrderByCreatedAtDesc(articleId).stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    private ArticleResponse mapToResponse(Article article) {
        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .thumbnail(article.getThumbnail())
                .authorId(article.getAuthor().getId())
                .authorName(article.getAuthor().getUsername())
                .status(article.getStatus())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();
    }

    private ArticleReviewResponse mapToReviewResponse(ArticleReview review) {
        return ArticleReviewResponse.builder()
                .id(review.getId())
                .articleId(review.getArticle().getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getUsername())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
