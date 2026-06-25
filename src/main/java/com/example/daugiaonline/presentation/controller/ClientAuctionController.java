package com.example.daugiaonline.presentation.controller;

import com.example.daugiaonline.application.dto.AuctionDto;
import com.example.daugiaonline.application.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
public class ClientAuctionController {

    private final AuctionService auctionService;

    @GetMapping("/my-won-auctions")
    public ResponseEntity<List<AuctionDto>> getMyWonAuctions(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(auctionService.getMyWonAuctions(userId));
    }

    @PostMapping("/pay-auction/{auctionId}")
    public ResponseEntity<?> payWonAuction(@PathVariable Long auctionId, @RequestParam("userId") Long userId) {
        auctionService.payWonAuction(userId, auctionId);
        return ResponseEntity.ok().body("{\"message\": \"Thanh toán thành công\"}");
    }
}
