package com.example.daugiaonline.presentation.controller;

import com.example.daugiaonline.application.dto.BidHistoryResponse;
import com.example.daugiaonline.application.dto.PlaceBidRequest;
import com.example.daugiaonline.application.service.BidService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@RestController
@RequestMapping("/api/v1/bids")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<BidHistoryResponse> placeBid(@Valid @RequestBody PlaceBidRequest request) {
        BidHistoryResponse response = bidService.placeBid(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // WebSocket endpoint cho việc đặt giá
    @MessageMapping("/auction/{id}/bid")
    public void placeBidWebSocket(@DestinationVariable("id") Long auctionId, @Valid PlaceBidRequest request) {
        // Đảm bảo request id khớp với url
        request.setAuctionId(auctionId);
        
        // Gọi service để lưu bid
        BidHistoryResponse response = bidService.placeBid(request);
        
        // Broadcast kết quả về cho tất cả clients đang subscribe topic này
        messagingTemplate.convertAndSend("/topic/auction/" + auctionId, response);
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<BidHistoryResponse>> getBidHistory(@PathVariable Long auctionId) {
        List<BidHistoryResponse> response = bidService.getBidHistory(auctionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BidHistoryResponse>> getAllBids() {
        return ResponseEntity.ok(bidService.getAllBids());
    }
}
