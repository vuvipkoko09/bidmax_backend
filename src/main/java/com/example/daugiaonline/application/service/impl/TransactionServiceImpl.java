package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.dto.TransactionDto;
import com.example.daugiaonline.application.dto.TransactionRequest;
import com.example.daugiaonline.application.service.TransactionService;
import com.example.daugiaonline.entity.Auction;
import com.example.daugiaonline.entity.Transaction;
import com.example.daugiaonline.entity.User;
import com.example.daugiaonline.enums.TransactionStatus;
import com.example.daugiaonline.exception.ResourceNotFoundException;
import com.example.daugiaonline.infrastructure.repository.AuctionRepository;
import com.example.daugiaonline.infrastructure.repository.TransactionRepository;
import com.example.daugiaonline.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;

    @Override
    @Transactional
    public TransactionDto createTransaction(TransactionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Auction auction = null;
        if (request.getAuctionId() != null) {
            auction = auctionRepository.findById(request.getAuctionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + request.getAuctionId()));
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .auction(auction)
                .amount(request.getAmount())
                .type(request.getType())
                .status(TransactionStatus.PENDING)
                .vnpayTranId(request.getVnpayTranId())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void updateTransactionStatus(Long id, TransactionStatus status) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        transaction.setStatus(status);
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionsByUser(Long userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionsByAuction(Long auctionId) {
        return transactionRepository.findByAuctionIdOrderByCreatedAtDesc(auctionId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private TransactionDto mapToDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .userId(transaction.getUser().getId())
                .username(transaction.getUser().getUsername())
                .auctionId(transaction.getAuction() != null ? transaction.getAuction().getId() : null)
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .vnpayTranId(transaction.getVnpayTranId())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
