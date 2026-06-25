package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.NewsRequest;
import com.example.daugiaonline.application.dto.NewsResponse;

import java.util.List;

public interface NewsService {
    NewsResponse createNews(NewsRequest request);
    List<NewsResponse> getAllNews();
    NewsResponse getNewsById(Long id);
}
