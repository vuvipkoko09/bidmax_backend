package com.example.daugiaonline.application.dto;

import com.example.daugiaonline.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    private Long orderId;
    private String auctionTitle;
    private String winnerName;
    private String sellerName;
    private String shippingAddress;
    private String trackingCode;
    private java.math.BigDecimal totalPrice;
    private OrderStatus status;
}
