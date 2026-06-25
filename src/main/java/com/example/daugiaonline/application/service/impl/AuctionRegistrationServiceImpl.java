package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.dto.AuctionRegistrationDto;
import com.example.daugiaonline.application.dto.AuctionRegistrationRequest;
import com.example.daugiaonline.application.service.AuctionRegistrationService;
import com.example.daugiaonline.entity.Auction;
import com.example.daugiaonline.entity.AuctionRegistration;
import com.example.daugiaonline.entity.User;
import com.example.daugiaonline.exception.BadRequestException;
import com.example.daugiaonline.exception.ResourceNotFoundException;
import com.example.daugiaonline.entity.Transaction;
import com.example.daugiaonline.enums.TransactionType;
import com.example.daugiaonline.enums.TransactionStatus;
import com.example.daugiaonline.infrastructure.repository.TransactionRepository;
import com.example.daugiaonline.infrastructure.repository.AuctionRegistrationRepository;
import com.example.daugiaonline.infrastructure.repository.AuctionRepository;
import com.example.daugiaonline.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuctionRegistrationServiceImpl implements AuctionRegistrationService {

    private final AuctionRegistrationRepository registrationRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public AuctionRegistrationDto registerForAuction(AuctionRegistrationRequest request) {
        if (registrationRepository.findByAuctionIdAndUserId(request.getAuctionId(), request.getUserId()).isPresent()) {
            throw new BadRequestException("User is already registered for this auction");
        }

        Auction auction = auctionRepository.findById(request.getAuctionId())
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + request.getAuctionId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        if (auction.getSeller().getId().equals(user.getId())) {
            throw new BadRequestException("Seller cannot register for their own auction");
        }

        double userBalance = user.getBalance() != null ? user.getBalance() : 0.0;
        if (userBalance < auction.getDepositAmount()) {
            throw new BadRequestException("Insufficient balance for deposit");
        }

        // Deduct deposit
        user.setBalance(userBalance - auction.getDepositAmount());
        userRepository.save(user);

        // Record transaction
        Transaction transaction = Transaction.builder()
                .user(user)
                .auction(auction)
                .amount(auction.getDepositAmount())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .build();
        transactionRepository.save(transaction);

        AuctionRegistration registration = AuctionRegistration.builder()
                .auction(auction)
                .user(user)
                .build();

        AuctionRegistration saved = registrationRepository.save(registration);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuctionRegistrationDto> getRegistrationsByAuction(Long auctionId) {
        return registrationRepository.findByAuctionId(auctionId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuctionRegistrationDto> getRegistrationsByUser(Long userId) {
        return registrationRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuctionRegistrationDto> getAllRegistrations() {
        return registrationRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private AuctionRegistrationDto mapToDto(AuctionRegistration registration) {
        return AuctionRegistrationDto.builder()
                .id(registration.getId())
                .auctionId(registration.getAuction().getId())
                .userId(registration.getUser().getId())
                .username(registration.getUser().getUsername())
                .registeredAt(registration.getRegisteredAt())
                .build();
    }
}
