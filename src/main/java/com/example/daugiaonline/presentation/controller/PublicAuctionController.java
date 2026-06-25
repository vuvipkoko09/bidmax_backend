package com.example.daugiaonline.presentation.controller;

import com.example.daugiaonline.application.dto.AuctionDto;
import com.example.daugiaonline.application.service.AuctionService;
import com.example.daugiaonline.enums.AuctionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/auctions")
@RequiredArgsConstructor
public class PublicAuctionController {

    private final AuctionService auctionService;

    @GetMapping
    public ResponseEntity<Page<AuctionDto>> searchAuctions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false, defaultValue = "ACTIVE") AuctionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<AuctionDto> result = auctionService.searchPublicAuctions(keyword, categoryId, minPrice, maxPrice, status, pageable);
        return ResponseEntity.ok(result);
    }
}
