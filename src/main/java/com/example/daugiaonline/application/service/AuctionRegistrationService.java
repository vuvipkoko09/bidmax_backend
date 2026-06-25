package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.AuctionRegistrationDto;
import com.example.daugiaonline.application.dto.AuctionRegistrationRequest;

import java.util.List;

public interface AuctionRegistrationService {
    AuctionRegistrationDto registerForAuction(AuctionRegistrationRequest request);
    List<AuctionRegistrationDto> getRegistrationsByAuction(Long auctionId);
    List<AuctionRegistrationDto> getRegistrationsByUser(Long userId);
    List<AuctionRegistrationDto> getAllRegistrations();
}
