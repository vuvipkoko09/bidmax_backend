package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.AuctionDto;
import com.example.daugiaonline.application.dto.AuctionRequest;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.daugiaonline.enums.AuctionStatus;
import java.util.List;

public interface AuctionService {
    List<AuctionDto> getAllAuctions(AuctionStatus status);

    AuctionDto getAuctionById(Long id);

    AuctionDto createAuction(AuctionRequest request);

    List<AuctionDto> getAuctionsBySeller(Long sellerId);

    AuctionDto createAuctionBySeller(AuctionRequest request, MultipartFile image, Long sellerId);

    AuctionDto updateAuction(Long id, AuctionRequest request);

    void deleteAuction(Long id);

    void processExpiredAuctions();
    void processUnpaidAuctions();
    
    List<AuctionDto> getMyWonAuctions(Long userId);

    void payWonAuction(Long userId, Long auctionId);

    Page<AuctionDto> searchPublicAuctions(String keyword, Long categoryId, Double minPrice, Double maxPrice, AuctionStatus status, Pageable pageable);
}
