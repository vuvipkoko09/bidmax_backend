package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.dto.ReviewRequest;
import com.example.daugiaonline.application.dto.ReviewResponse;
import com.example.daugiaonline.application.service.ReviewService;
import com.example.daugiaonline.entity.Auction;
import com.example.daugiaonline.entity.Review;
import com.example.daugiaonline.entity.User;
import com.example.daugiaonline.exception.ResourceNotFoundException;
import com.example.daugiaonline.infrastructure.repository.AuctionRepository;
import com.example.daugiaonline.infrastructure.repository.ReviewRepository;
import com.example.daugiaonline.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewResponse addReview(ReviewRequest request) {
        Auction auction = auctionRepository.findById(request.getAuctionId())
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + request.getAuctionId()));

        User reviewer = userRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found with id: " + request.getReviewerId()));

        User reviewee = userRepository.findById(request.getRevieweeId())
                .orElseThrow(() -> new ResourceNotFoundException("Reviewee not found with id: " + request.getRevieweeId()));

        Review review = Review.builder()
                .auction(auction)
                .reviewer(reviewer)
                .reviewee(reviewee)
                .ratingStar(request.getRatingStar())
                .comment(request.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);

        return mapToResponse(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getUserReviews(Long userId) {
        // Find reviews where the user is the reviewee (the one who gets reviewed)
        return reviewRepository.findByRevieweeId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .reviewerName(review.getReviewer() != null ? review.getReviewer().getUsername() : null)
                .ratingStar(review.getRatingStar())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
