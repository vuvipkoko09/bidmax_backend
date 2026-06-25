package com.example.daugiaonline.application.dto;

import com.example.daugiaonline.enums.AuctionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 255, message = "Title must be between 10 and 255 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Start price is required")
    @Positive(message = "Start price must be positive")
    private Double startPrice;

    @NotNull(message = "Step price is required")
    @Positive(message = "Step price must be positive")
    private Double stepPrice;

    @NotNull(message = "Deposit amount is required")
    @Positive(message = "Deposit amount must be positive")
    private Double depositAmount;

    @NotNull(message = "Seller ID is required")
    private Long sellerId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Location ID is required")
    private Long locationId;

    @NotNull(message = "Status is required")
    private AuctionStatus status;

    @NotNull(message = "Registration start time is required")
    private LocalDateTime regStartTime;

    @NotNull(message = "Registration end time is required")
    private LocalDateTime regEndTime;

    @NotNull(message = "Bid start time is required")
    private LocalDateTime bidStartTime;

    @NotNull(message = "Bid end time is required")
    private LocalDateTime bidEndTime;
}
