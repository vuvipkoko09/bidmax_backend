package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.ReviewRequest;
import com.example.daugiaonline.application.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse addReview(ReviewRequest request);
    List<ReviewResponse> getUserReviews(Long userId);
    List<ReviewResponse> getAllReviews();
}
