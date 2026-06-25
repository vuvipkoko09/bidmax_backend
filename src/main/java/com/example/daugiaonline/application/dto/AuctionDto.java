package com.example.daugiaonline.application.dto;

import com.example.daugiaonline.enums.AuctionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionDto {
    private Long id;
    private String title;
    private String thumbnail;
    private String description;
    private Double startPrice;
    private Double currentPrice;
    private Double stepPrice;
    private Double depositAmount;
    private Long sellerId;
    private String sellerName;
    private Long categoryId;
    private String categoryName;
    private Long locationId;
    private String locationName;
    private AuctionStatus status;
    private LocalDateTime regStartTime;
    private LocalDateTime regEndTime;
    private LocalDateTime bidStartTime;
    private LocalDateTime bidEndTime;
}
