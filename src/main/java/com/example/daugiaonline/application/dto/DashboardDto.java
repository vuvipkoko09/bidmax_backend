package com.example.daugiaonline.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {
    private long totalAuctions;
    private long activeAuctions;
    private long completedAuctions; // Bán thành công (SOLD, PAID, SHIPPED, COMPLETED)
    private double totalRevenue;
    private long totalUsers;

    private List<Map<String, Object>> auctionsByStatus;
    private List<Map<String, Object>> revenueByMonth;
}
