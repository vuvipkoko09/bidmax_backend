package com.example.daugiaonline.presentation.controller;

import com.example.daugiaonline.application.dto.AuctionDto;
import com.example.daugiaonline.application.dto.AuctionRequest;
import com.example.daugiaonline.application.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seller/auctions")
@RequiredArgsConstructor
public class SellerAuctionController {

    private final AuctionService auctionService;

    @GetMapping
    public ResponseEntity<List<AuctionDto>> getSellerAuctions(@RequestParam("sellerId") Long sellerId) {
        // Trong hệ thống hoàn chỉnh có Spring Security, ta sẽ lấy sellerId từ SecurityContextHolder
        return ResponseEntity.ok(auctionService.getAuctionsBySeller(sellerId));
    }

    @PostMapping
    public ResponseEntity<AuctionDto> createSellerAuction(
            @ModelAttribute AuctionRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        
        Long sellerId = request.getSellerId();
        AuctionDto response = auctionService.createAuctionBySeller(request, image, sellerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
