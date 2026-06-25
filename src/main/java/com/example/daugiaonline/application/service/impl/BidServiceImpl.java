package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.dto.BidHistoryResponse;
import com.example.daugiaonline.application.dto.PlaceBidRequest;
import com.example.daugiaonline.application.service.BidService;
import com.example.daugiaonline.application.service.EmailService;
import com.example.daugiaonline.entity.Auction;
import com.example.daugiaonline.entity.Bid;
import com.example.daugiaonline.entity.User;
import com.example.daugiaonline.exception.BadRequestException;
import com.example.daugiaonline.exception.ResourceNotFoundException;
import com.example.daugiaonline.infrastructure.repository.AuctionRepository;
import com.example.daugiaonline.infrastructure.repository.BidRepository;
import com.example.daugiaonline.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.daugiaonline.enums.AuctionStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final com.example.daugiaonline.infrastructure.repository.AuctionRegistrationRepository registrationRepository;

    @Override
    @Transactional
    public BidHistoryResponse placeBid(PlaceBidRequest request) {
        Auction auction = auctionRepository.findById(request.getAuctionId())
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + request.getAuctionId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new BadRequestException("Auction is not active");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(auction.getBidStartTime()) || now.isAfter(auction.getBidEndTime())) {
            throw new BadRequestException("Bidding is currently closed for this auction");
        }

        if (auction.getSeller().getId().equals(user.getId())) {
            throw new BadRequestException("Seller cannot bid on their own auction");
        }

        if (registrationRepository.findByAuctionIdAndUserId(auction.getId(), user.getId()).isEmpty()) {
            throw new BadRequestException("User must register for the auction before bidding");
        }

        if (request.getBidAmount() < auction.getCurrentPrice() + auction.getStepPrice()) {
            throw new BadRequestException("Bid amount must be at least current price plus step price");
        }

        // Tìm người đặt giá cao nhất hiện tại
        List<Bid> currentBids = bidRepository.findByAuctionIdOrderByBidAmountDesc(auction.getId());
        User previousHighestUser = null;
        if (!currentBids.isEmpty()) {
            previousHighestUser = currentBids.get(0).getUser();
        }

        auction.setCurrentPrice(request.getBidAmount());
        auctionRepository.save(auction);

        Bid bid = Bid.builder()
                .auction(auction)
                .user(user)
                .bidAmount(request.getBidAmount())
                .build();

        Bid savedBid = bidRepository.save(bid);

        // Gửi email nếu bị trả giá đè
        if (previousHighestUser != null && !previousHighestUser.getId().equals(user.getId())) {
            String subject = "Cảnh báo: Bạn vừa mất vị trí dẫn đầu!";
            String htmlBody = "<div style='font-family: Arial, sans-serif;'>" +
                    "<h2 style='color: #e53e3e;'>Bị trả giá đè!</h2>" +
                    "<p>Chào <strong>" + previousHighestUser.getUsername() + "</strong>,</p>" +
                    "<p>Rất tiếc, một người dùng khác vừa đặt giá cao hơn bạn cho sản phẩm <strong>" + auction.getTitle() + "</strong>.</p>" +
                    "<p>Giá hiện tại đang là: <strong style='color: #3182ce;'>" + request.getBidAmount() + " VNĐ</strong></p>" +
                    "<p>Đừng bỏ lỡ sản phẩm yêu thích của bạn! Hãy nhấn vào nút bên dưới để quay lại sàn đấu giá:</p>" +
                    "<a href='http://localhost:5173/auction/" + auction.getId() + "' style='display: inline-block; padding: 10px 20px; background-color: #3182ce; color: white; text-decoration: none; border-radius: 5px; font-weight: bold;'>ĐẶT GIÁ LẠI NGAY</a>" +
                    "</div>";
            emailService.sendHtmlEmail(previousHighestUser.getEmail(), subject, htmlBody);
        }

        return mapToResponse(savedBid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BidHistoryResponse> getBidHistory(Long auctionId) {
        return bidRepository.findByAuctionIdOrderByBidAmountDesc(auctionId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BidHistoryResponse> getAllBids() {
        return bidRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BidHistoryResponse mapToResponse(Bid bid) {
        return BidHistoryResponse.builder()
                .id(bid.getId())
                .userName(bid.getUser() != null ? bid.getUser().getUsername() : null)
                .bidAmount(bid.getBidAmount())
                .bidTime(bid.getBidTime())
                .build();
    }
}
