package com.example.daugiaonline.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionDepositRequest {
    @NotNull(message = "Auction ID is required")
    private Long auctionId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Deposit amount is required")
    @Positive(message = "Deposit amount must be positive")
    private Double depositAmount;
}
