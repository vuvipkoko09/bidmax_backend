package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.TransactionDto;
import com.example.daugiaonline.application.dto.TransactionRequest;
import com.example.daugiaonline.enums.TransactionStatus;

import java.util.List;

public interface TransactionService {
    TransactionDto createTransaction(TransactionRequest request);
    void updateTransactionStatus(Long id, TransactionStatus status);
    List<TransactionDto> getTransactionsByUser(Long userId);
    List<TransactionDto> getTransactionsByAuction(Long auctionId);
    List<TransactionDto> getAllTransactions();
}
