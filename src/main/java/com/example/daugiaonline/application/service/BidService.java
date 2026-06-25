package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.BidHistoryResponse;
import com.example.daugiaonline.application.dto.PlaceBidRequest;

import java.util.List;

public interface BidService {
    BidHistoryResponse placeBid(PlaceBidRequest request);
    List<BidHistoryResponse> getBidHistory(Long auctionId);
    List<BidHistoryResponse> getAllBids();
}
