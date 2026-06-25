package com.example.daugiaonline.application.dto;

import com.example.daugiaonline.enums.DepositStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionDepositDto {
    private Long id;
    private Long auctionId;
    private Long userId;
    private String username;
    private Double depositAmount;
    private DepositStatus status;
    private LocalDateTime createdAt;
}
