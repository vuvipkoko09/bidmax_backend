package com.example.daugiaonline.application.scheduler;

import com.example.daugiaonline.application.service.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionScheduler {

    private final AuctionService auctionService;

    // Chạy mỗi 60 giây (60000ms)
    @Scheduled(fixedRate = 60000)
    public void checkExpiredAuctions() {
        log.info("Running scheduled task to check and close expired auctions...");
        try {
            auctionService.processExpiredAuctions();
        } catch (Exception e) {
            log.error("Error occurred while processing expired auctions: ", e);
        }
    }

    // Chạy mỗi giờ (3600000ms) để kiểm tra các phiên đấu giá bùng kèo
    @Scheduled(fixedRate = 3600000)
    public void checkUnpaidAuctions() {
        log.info("Running scheduled task to check for unpaid auctions...");
        try {
            auctionService.processUnpaidAuctions();
        } catch (Exception e) {
            log.error("Error occurred while processing unpaid auctions: ", e);
        }
    }
}
