package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.dto.AuctionDepositDto;
import com.example.daugiaonline.application.dto.AuctionDepositRequest;
import com.example.daugiaonline.application.service.AuctionDepositService;
import com.example.daugiaonline.entity.Auction;
import com.example.daugiaonline.entity.AuctionDeposit;
import com.example.daugiaonline.entity.User;
import com.example.daugiaonline.enums.DepositStatus;
import com.example.daugiaonline.exception.BadRequestException;
import com.example.daugiaonline.exception.ResourceNotFoundException;
import com.example.daugiaonline.infrastructure.repository.AuctionDepositRepository;
import com.example.daugiaonline.infrastructure.repository.AuctionRepository;
import com.example.daugiaonline.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuctionDepositServiceImpl implements AuctionDepositService {

    private final AuctionDepositRepository depositRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AuctionDepositDto makeDeposit(AuctionDepositRequest request) {
        if (depositRepository.findByAuctionIdAndUserId(request.getAuctionId(), request.getUserId()).isPresent()) {
            throw new BadRequestException("User has already made a deposit for this auction");
        }

        Auction auction = auctionRepository.findById(request.getAuctionId())
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + request.getAuctionId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        AuctionDeposit deposit = AuctionDeposit.builder()
                .auction(auction)
                .user(user)
                .depositAmount(request.getDepositAmount())
                .status(DepositStatus.LOCKED) // Default to locked when deposit is made
                .build();

        AuctionDeposit saved = depositRepository.save(deposit);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void updateDepositStatus(Long id, DepositStatus status) {
        AuctionDeposit deposit = depositRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deposit not found with id: " + id));
        deposit.setStatus(status);
        depositRepository.save(deposit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuctionDepositDto> getDepositsByAuction(Long auctionId) {
        return depositRepository.findByAuctionId(auctionId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuctionDepositDto> getDepositsByUser(Long userId) {
        return depositRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuctionDepositDto> getAllDeposits() {
        return depositRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private AuctionDepositDto mapToDto(AuctionDeposit deposit) {
        return AuctionDepositDto.builder()
                .id(deposit.getId())
                .auctionId(deposit.getAuction().getId())
                .userId(deposit.getUser().getId())
                .username(deposit.getUser().getUsername())
                .depositAmount(deposit.getDepositAmount())
                .status(deposit.getStatus())
                .createdAt(deposit.getCreatedAt())
                .build();
    }
}
