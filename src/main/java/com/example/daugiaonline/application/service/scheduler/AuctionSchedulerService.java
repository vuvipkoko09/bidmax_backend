package com.example.daugiaonline.application.service.scheduler;

import com.example.daugiaonline.application.service.EmailService;
import com.example.daugiaonline.entity.Auction;
import com.example.daugiaonline.entity.Bid;
import com.example.daugiaonline.enums.AuctionStatus;
import com.example.daugiaonline.infrastructure.repository.AuctionRepository;
import com.example.daugiaonline.infrastructure.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionSchedulerService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final EmailService emailService;

    @Transactional
    @Scheduled(cron = "0 * * * * ?") // Chạy mỗi phút vào giây thứ 0
    public void processExpiredAuctions() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Auction> expiredAuctions = auctionRepository.findByStatusAndBidEndTimeBefore(AuctionStatus.ACTIVE, currentTime);

        if (expiredAuctions.isEmpty()) {
            return;
        }

        log.info("Bắt đầu quét và chốt các phiên đấu giá quá hạn: Tìm thấy {} phiên.", expiredAuctions.size());

        for (Auction auction : expiredAuctions) {
            List<Bid> bids = bidRepository.findByAuctionIdOrderByBidAmountDesc(auction.getId());

            if (!bids.isEmpty()) {
                Bid highestBid = bids.get(0);
                auction.setStatus(AuctionStatus.SOLD);
                auction.setWinner(highestBid.getUser());
                auction.setWinningPrice(highestBid.getBidAmount());
                log.info("Đã chốt phiên [{}]. Người thắng: ID {}, Giá: {}",
                        auction.getTitle(), highestBid.getUser().getId(), highestBid.getBidAmount());

                // Gửi email chúc mừng
                if (highestBid.getUser() != null && highestBid.getUser().getEmail() != null) {
                    String subject = "Chúc mừng! Bạn đã thắng phiên đấu giá: " + auction.getTitle();
                    String htmlBody = "<div style='font-family: Arial, sans-serif;'>" +
                            "<h2 style='color: #38a169;'>Chiến thắng rực rỡ!</h2>" +
                            "<p>Chào <strong>" + highestBid.getUser().getUsername() + "</strong>,</p>" +
                            "<p>Chúc mừng bạn đã chính thức là chủ nhân của sản phẩm <strong>" + auction.getTitle() + "</strong>.</p>" +
                            "<p>Mức giá chốt: <strong style='color: #e53e3e;'>" + highestBid.getBidAmount() + " VNĐ</strong></p>" +
                            "<p>Vui lòng đăng nhập vào hệ thống để tiến hành thanh toán và nhận hàng.</p>" +
                            "<a href='http://localhost:5173/orders' style='display: inline-block; padding: 10px 20px; background-color: #38a169; color: white; text-decoration: none; border-radius: 5px; font-weight: bold;'>THANH TOÁN NGAY</a>" +
                            "</div>";
                    emailService.sendHtmlEmail(highestBid.getUser().getEmail(), subject, htmlBody);
                }

            } else {
                auction.setStatus(AuctionStatus.UNSOLD);
                log.info("Phiên [{}] thất bại do không có lượt đặt giá.", auction.getTitle());
            }
        }

        auctionRepository.saveAll(expiredAuctions);
        log.info("Đã lưu trạng thái các phiên đấu giá quá hạn vào cơ sở dữ liệu.");
    }
}
