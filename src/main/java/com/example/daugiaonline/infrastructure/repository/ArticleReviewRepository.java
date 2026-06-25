package com.example.daugiaonline.infrastructure.repository;

import com.example.daugiaonline.entity.ArticleReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleReviewRepository extends JpaRepository<ArticleReview, Long> {
    List<ArticleReview> findByArticleIdOrderByCreatedAtDesc(Long articleId);
}
