package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.dto.NewsRequest;
import com.example.daugiaonline.application.dto.NewsResponse;
import com.example.daugiaonline.application.service.NewsService;
import com.example.daugiaonline.entity.News;
import com.example.daugiaonline.entity.User;
import com.example.daugiaonline.exception.ResourceNotFoundException;
import com.example.daugiaonline.infrastructure.repository.NewsRepository;
import com.example.daugiaonline.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public NewsResponse createNews(NewsRequest request) {
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + request.getAuthorId()));

        News news = News.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .publishedAt(LocalDateTime.now())
                .build();

        News saved = newsRepository.save(news);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsResponse> getAllNews() {
        return newsRepository.findAllByOrderByPublishedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NewsResponse getNewsById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found with id: " + id));
        return mapToResponse(news);
    }

    private NewsResponse mapToResponse(News news) {
        return NewsResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .authorName(news.getAuthor() != null ? news.getAuthor().getUsername() : null)
                .publishedAt(news.getPublishedAt())
                .build();
    }
}
