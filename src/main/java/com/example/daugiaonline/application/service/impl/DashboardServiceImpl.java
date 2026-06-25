package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.dto.DashboardDto;
import com.example.daugiaonline.application.service.DashboardService;
import com.example.daugiaonline.enums.AuctionStatus;
import com.example.daugiaonline.infrastructure.repository.AuctionRepository;
import com.example.daugiaonline.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardDto getDashboardStats(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime end = (endDate != null) ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now().plusYears(100);

        long totalAuctions = auctionRepository.countByCreatedAtBetween(start, end);
        long activeAuctions = auctionRepository.countByStatusAndCreatedAtBetween(AuctionStatus.ACTIVE, start, end);
        
        List<AuctionStatus> soldStatuses = Arrays.asList(AuctionStatus.SOLD, AuctionStatus.PAID, AuctionStatus.SHIPPED, AuctionStatus.COMPLETED);
        long completedAuctions = auctionRepository.countByStatusInAndCreatedAtBetween(soldStatuses, start, end);
        
        Double revenue = auctionRepository.sumRevenueByStatusInAndCreatedAtBetween(soldStatuses, start, end);
        double totalRevenue = (revenue != null) ? revenue : 0.0;

        long totalUsers = userRepository.countByCreatedAtBetween(start, end);

        // Calculate Auctions By Status
        List<Map<String, Object>> auctionsByStatus = new ArrayList<>();
        long pendingCount = auctionRepository.countByStatusAndCreatedAtBetween(AuctionStatus.PENDING, start, end);
        long failedCount = auctionRepository.countByStatusAndCreatedAtBetween(AuctionStatus.FAILED, start, end);
        
        auctionsByStatus.add(Map.of("name", "Pending", "value", pendingCount));
        auctionsByStatus.add(Map.of("name", "Active", "value", activeAuctions));
        auctionsByStatus.add(Map.of("name", "Completed", "value", completedAuctions));
        // auctionsByStatus.add(Map.of("name", "Failed", "value", failedCount)); // Optional

        // Calculate Revenue By Month (Last 6 Months)
        List<Map<String, Object>> revenueByMonth = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth targetMonth = currentMonth.minusMonths(i);
            LocalDateTime monthStart = targetMonth.atDay(1).atStartOfDay();
            LocalDateTime monthEnd = targetMonth.atEndOfMonth().atTime(LocalTime.MAX);
            
            Double monthRevenue = auctionRepository.sumRevenueByStatusInAndCreatedAtBetween(soldStatuses, monthStart, monthEnd);
            
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("name", "T" + targetMonth.getMonthValue());
            monthData.put("revenue", (monthRevenue != null) ? monthRevenue : 0.0);
            revenueByMonth.add(monthData);
        }

        return DashboardDto.builder()
                .totalAuctions(totalAuctions)
                .activeAuctions(activeAuctions)
                .completedAuctions(completedAuctions)
                .totalRevenue(totalRevenue)
                .totalUsers(totalUsers)
                .auctionsByStatus(auctionsByStatus)
                .revenueByMonth(revenueByMonth)
                .build();
    }
}
