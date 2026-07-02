package com.example.daugiaonline.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistDto {
    private Long id;
    private Long userId;
    private Long auctionId;
    private String auctionTitle;
    private Double currentPrice;
    private String status;
    private String thumbnail;
    private LocalDateTime addedAt;
}
