package com.example.daugiaonline.infrastructure.repository;

import com.example.daugiaonline.entity.Article;
import com.example.daugiaonline.enums.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByStatusOrderByCreatedAtDesc(ArticleStatus status);
}
