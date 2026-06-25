package com.example.daugiaonline.presentation.controller;

import com.example.daugiaonline.application.dto.TransactionDto;
import com.example.daugiaonline.application.dto.TransactionRequest;
import com.example.daugiaonline.application.service.TransactionService;
import com.example.daugiaonline.enums.TransactionStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateTransactionStatus(@PathVariable Long id, @RequestParam TransactionStatus status) {
        transactionService.updateTransactionStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getTransactionsByUser(userId));
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByAuction(@PathVariable Long auctionId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAuction(auctionId));
    }

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
}
