package com.example.daugiaonline.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionImageDto {
    private Long id;

    @NotNull(message = "Auction ID is required")
    private Long auctionId;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private Boolean isPrimary;
}
