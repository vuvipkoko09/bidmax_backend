package com.example.daugiaonline.application.dto;

import com.example.daugiaonline.enums.TransactionStatus;
import com.example.daugiaonline.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private Long userId;
    private String username;
    private Long auctionId;
    private Double amount;
    private TransactionType type;
    private TransactionStatus status;
    private String vnpayTranId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
