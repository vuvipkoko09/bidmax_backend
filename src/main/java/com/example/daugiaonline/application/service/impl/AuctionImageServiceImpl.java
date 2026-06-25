package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.dto.AuctionImageDto;
import com.example.daugiaonline.application.service.AuctionImageService;
import com.example.daugiaonline.entity.Auction;
import com.example.daugiaonline.entity.AuctionImage;
import com.example.daugiaonline.exception.ResourceNotFoundException;
import com.example.daugiaonline.infrastructure.repository.AuctionImageRepository;
import com.example.daugiaonline.infrastructure.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuctionImageServiceImpl implements AuctionImageService {

    private final AuctionImageRepository auctionImageRepository;
    private final AuctionRepository auctionRepository;

    @Override
    @Transactional
    public AuctionImageDto addImage(AuctionImageDto request) {
        Auction auction = auctionRepository.findById(request.getAuctionId())
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + request.getAuctionId()));

        AuctionImage image = AuctionImage.builder()
                .auction(auction)
                .imageUrl(request.getImageUrl())
                .isPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false)
                .build();

        AuctionImage saved = auctionImageRepository.save(image);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void removeImage(Long id) {
        if (!auctionImageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Auction image not found with id: " + id);
        }
        auctionImageRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuctionImageDto> getAuctionImages(Long auctionId) {
        return auctionImageRepository.findByAuctionId(auctionId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuctionImageDto> getAllImages() {
        return auctionImageRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private AuctionImageDto mapToDto(AuctionImage image) {
        return AuctionImageDto.builder()
                .id(image.getId())
                .auctionId(image.getAuction().getId())
                .imageUrl(image.getImageUrl())
                .isPrimary(image.getIsPrimary())
                .build();
    }
}
