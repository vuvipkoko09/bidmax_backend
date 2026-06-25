package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.AuctionDepositDto;
import com.example.daugiaonline.application.dto.AuctionDepositRequest;
import com.example.daugiaonline.enums.DepositStatus;

import java.util.List;

public interface AuctionDepositService {
    AuctionDepositDto makeDeposit(AuctionDepositRequest request);
    void updateDepositStatus(Long id, DepositStatus status);
    List<AuctionDepositDto> getDepositsByAuction(Long auctionId);
    List<AuctionDepositDto> getDepositsByUser(Long userId);
    List<AuctionDepositDto> getAllDeposits();
}
