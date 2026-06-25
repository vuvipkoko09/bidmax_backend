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
public class AuctionRegistrationDto {
    private Long id;
    private Long auctionId;
    private Long userId;
    private String username;
    private LocalDateTime registeredAt;
}
