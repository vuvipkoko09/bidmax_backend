package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.AuctionImageDto;

import java.util.List;

public interface AuctionImageService {
    AuctionImageDto addImage(AuctionImageDto request);
    void removeImage(Long id);
    List<AuctionImageDto> getAuctionImages(Long auctionId);
    List<AuctionImageDto> getAllImages();
}
