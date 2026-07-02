package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.dto.AuctionDto;
import com.example.daugiaonline.application.dto.AuctionRequest;
import com.example.daugiaonline.application.service.AuctionService;
import com.example.daugiaonline.application.service.FileUploadService;
import com.example.daugiaonline.entity.Auction;
import com.example.daugiaonline.entity.Category;
import com.example.daugiaonline.entity.Location;
import com.example.daugiaonline.entity.User;
import com.example.daugiaonline.exception.ResourceNotFoundException;
import com.example.daugiaonline.infrastructure.repository.AuctionRepository;
import com.example.daugiaonline.infrastructure.repository.CategoryRepository;
import com.example.daugiaonline.infrastructure.repository.LocationRepository;
import com.example.daugiaonline.infrastructure.repository.UserRepository;
import com.example.daugiaonline.infrastructure.repository.BidRepository;
import com.example.daugiaonline.infrastructure.repository.OrderRepository;
import com.example.daugiaonline.infrastructure.repository.NotificationRepository;
import com.example.daugiaonline.application.service.EmailService;
import com.example.daugiaonline.enums.AuctionStatus;
import com.example.daugiaonline.entity.Bid;
import com.example.daugiaonline.entity.Order;
import com.example.daugiaonline.entity.Notification;
import com.example.daugiaonline.enums.OrderStatus;
import com.example.daugiaonline.infrastructure.repository.AuctionRegistrationRepository;
import com.example.daugiaonline.entity.AuctionRegistration;
import com.example.daugiaonline.entity.Transaction;
import com.example.daugiaonline.enums.TransactionType;
import com.example.daugiaonline.enums.TransactionStatus;
import com.example.daugiaonline.infrastructure.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;
import com.example.daugiaonline.exception.BadRequestException;
import com.example.daugiaonline.exception.AppException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final FileUploadService fileUploadService;
    private final BidRepository bidRepository;
    private final OrderRepository orderRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final AuctionRegistrationRepository registrationRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public List<AuctionDto> getAllAuctions(AuctionStatus status) {
        List<Auction> auctions = (status != null) 
            ? auctionRepository.findByStatusOrderByIdDesc(status) 
            : auctionRepository.findAll();
            
        return auctions.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AuctionDto getAuctionById(Long id) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + id));
        return mapToDto(auction);
    }

    @Override
    @Transactional
    public AuctionDto createAuction(AuctionRequest request) {
        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + request.getSellerId()));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + request.getLocationId()));

        Auction auction = Auction.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startPrice(request.getStartPrice())
                .currentPrice(request.getStartPrice()) // Initial current price equals start price
                .stepPrice(request.getStepPrice())
                .depositAmount(request.getDepositAmount())
                .seller(seller)
                .category(category)
                .location(location)
                .status(request.getStatus())
                .regStartTime(request.getRegStartTime())
                .regEndTime(request.getRegEndTime())
                .bidStartTime(request.getBidStartTime())
                .bidEndTime(request.getBidEndTime())
                .build();

        return mapToDto(auctionRepository.save(auction));
    }

    @Override
    public List<AuctionDto> getAuctionsBySeller(Long sellerId) {
        return auctionRepository.findBySellerIdOrderByIdDesc(sellerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AuctionDto createAuctionBySeller(AuctionRequest request, MultipartFile image, Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + sellerId));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        
        // Default location to 1 if not provided
        Long locId = request.getLocationId() != null ? request.getLocationId() : 1L;
        Location location = locationRepository.findById(locId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + locId));

        String thumbnailUrl = null;
        if (image != null && !image.isEmpty()) {
            thumbnailUrl = fileUploadService.uploadFile(image);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime regStart = request.getRegStartTime() != null ? request.getRegStartTime() : now;
        LocalDateTime regEnd = request.getRegEndTime() != null ? request.getRegEndTime() : (request.getBidEndTime() != null ? request.getBidEndTime().minusDays(1) : now.plusDays(6));
        LocalDateTime bidStart = request.getBidStartTime() != null ? request.getBidStartTime() : now;
        LocalDateTime bidEnd = request.getBidEndTime() != null ? request.getBidEndTime() : now.plusDays(7);

        if (regEnd.isBefore(regStart) || regEnd.isEqual(regStart)) {
            throw new BadRequestException("Thời gian kết thúc đăng ký phải lớn hơn thời gian bắt đầu đăng ký");
        }
        if (bidStart.isBefore(regEnd)) {
            throw new BadRequestException("Thời gian bắt đầu đấu giá phải sau hoặc bằng thời gian kết thúc đăng ký");
        }
        if (bidEnd.isBefore(bidStart) || bidEnd.isEqual(bidStart)) {
            throw new BadRequestException("Thời gian kết thúc đấu giá phải lớn hơn thời gian bắt đầu");
        }
        if (bidEnd.isBefore(now)) {
            throw new BadRequestException("Thời gian kết thúc đấu giá không được ở trong quá khứ");
        }

        Auction auction = Auction.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startPrice(request.getStartPrice())
                .currentPrice(request.getStartPrice())
                .stepPrice(request.getStepPrice())
                .depositAmount(request.getDepositAmount() != null ? request.getDepositAmount() : request.getStartPrice() * 0.1)
                .seller(seller)
                .category(category)
                .location(location)
                .status(com.example.daugiaonline.enums.AuctionStatus.PENDING)
                .regStartTime(regStart)
                .regEndTime(regEnd)
                .bidStartTime(bidStart)
                .bidEndTime(bidEnd)
                .thumbnail(thumbnailUrl)
                .build();

        return mapToDto(auctionRepository.save(auction));
    }

    @Override
    @Transactional
    public AuctionDto updateAuction(Long id, AuctionRequest request) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + id));

        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + request.getSellerId()));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + request.getLocationId()));

        auction.setTitle(request.getTitle());
        auction.setDescription(request.getDescription());
        auction.setStartPrice(request.getStartPrice());
        auction.setStepPrice(request.getStepPrice());
        auction.setDepositAmount(request.getDepositAmount());
        auction.setSeller(seller);
        auction.setCategory(category);
        auction.setLocation(location);
        auction.setStatus(request.getStatus());
        auction.setRegStartTime(request.getRegStartTime());
        auction.setRegEndTime(request.getRegEndTime());
        auction.setBidStartTime(request.getBidStartTime());
        auction.setBidEndTime(request.getBidEndTime());

        return mapToDto(auctionRepository.save(auction));
    }

    @Override
    @Transactional
    public void deleteAuction(Long id) {
        if (!auctionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Auction not found with id: " + id);
        }
        List<AuctionRegistration> registrations = registrationRepository.findByAuctionId(id);
        if (!registrations.isEmpty()) {
            throw new BadRequestException("Không thể xóa phiên đấu giá đã có người đăng ký tham gia");
        }
        auctionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void processExpiredAuctions() {
        List<Auction> expiredAuctions = auctionRepository.findByStatusAndBidEndTimeBefore(AuctionStatus.ACTIVE, LocalDateTime.now());
        for (Auction auction : expiredAuctions) {
            
            // NGAY LẬP TỨC kiểm tra lại điều kiện để loại bỏ các case đã xử lý (Double check)
            if (auction.getStatus() != AuctionStatus.ACTIVE) {
                continue;
            }

            List<Bid> bids = bidRepository.findByAuctionIdOrderByBidAmountDesc(auction.getId());
            List<AuctionRegistration> registrations = registrationRepository.findByAuctionId(auction.getId());
            User winner = null;
            
            if (!bids.isEmpty()) {
                Bid highestBid = bids.get(0);
                winner = highestBid.getUser();
                auction.setStatus(AuctionStatus.SOLD);
                auction.setWinner(winner);
                auction.setWinningPrice(highestBid.getBidAmount());
                // CHỐT TRẠNG THÁI XUỐNG DB NGAY LẬP TỨC
                auctionRepository.save(auction);

                Order order = Order.builder()
                        .auction(auction)
                        .winner(winner)
                        .seller(auction.getSeller())
                        .shippingAddress(winner.getAddress() != null ? winner.getAddress() : "Chưa cập nhật")
                        .totalPrice(java.math.BigDecimal.valueOf(highestBid.getBidAmount()))
                        .status(OrderStatus.PENDING)
                        .build();
                orderRepository.save(order);

                Notification winnerNotif = Notification.builder()
                        .user(winner)
                        .message("Chúc mừng! Bạn đã thắng phiên đấu giá: " + auction.getTitle())
                        .build();
                notificationRepository.save(winnerNotif);

                Notification sellerNotif = Notification.builder()
                        .user(auction.getSeller())
                        .message("Phiên đấu giá " + auction.getTitle() + " đã được bán thành công!")
                        .build();
                notificationRepository.save(sellerNotif);

                try {
                    emailService.sendHtmlEmail(winner.getEmail(), "Kết quả đấu giá: THẮNG", "Bạn đã thắng phiên đấu giá " + auction.getTitle());
                    emailService.sendHtmlEmail(auction.getSeller().getEmail(), "Kết quả đấu giá: ĐÃ BÁN", "Phiên đấu giá " + auction.getTitle() + " đã có người mua.");
                } catch (Exception e) {
                    log.error("Lỗi khi gửi email thông báo trúng đấu giá: ", e);
                }
            } else {
                auction.setStatus(AuctionStatus.UNSOLD);
                // CHỐT TRẠNG THÁI XUỐNG DB NGAY LẬP TỨC
                auctionRepository.save(auction);
                
                Notification sellerNotif = Notification.builder()
                        .user(auction.getSeller())
                        .message("Phiên đấu giá " + auction.getTitle() + " đã kết thúc mà không có người trả giá.")
                        .build();
                notificationRepository.save(sellerNotif);
                
                try {
                    emailService.sendHtmlEmail(auction.getSeller().getEmail(), "Kết quả đấu giá: KHÔNG THÀNH CÔNG", "Phiên đấu giá " + auction.getTitle() + " không có người trả giá.");
                } catch (Exception e) {
                    log.error("Lỗi khi gửi email thông báo không thành công: ", e);
                }
            }
            
            // Hoàn cọc cho người thua hoặc cho mọi người nếu UNSOLD
            for (AuctionRegistration reg : registrations) {
                if (winner == null || !reg.getUser().getId().equals(winner.getId())) {
                    User loser = reg.getUser();
                    double currentBalance = loser.getBalance() != null ? loser.getBalance() : 0.0;
                    loser.setBalance(currentBalance + auction.getDepositAmount());
                    userRepository.save(loser);
                    
                    Transaction transaction = Transaction.builder()
                            .user(loser)
                            .auction(auction)
                            .amount(auction.getDepositAmount())
                            .type(TransactionType.REFUND)
                            .status(TransactionStatus.SUCCESS)
                            .build();
                    transactionRepository.save(transaction);
                }
            }
        }
    }

    @Override
    @Transactional
    public void processUnpaidAuctions() {
        // Find auctions that have been SOLD but bidEndTime is older than 3 days
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(3);
        List<Auction> unpaidAuctions = auctionRepository.findUnpaidAuctions(AuctionStatus.SOLD, cutoffTime);
        for (Auction auction : unpaidAuctions) {
            User defaulter = auction.getWinner();
            
            auction.setStatus(AuctionStatus.UNSOLD);
            auction.setWinner(null);
            auctionRepository.save(auction);
            
            if (defaulter != null) {
                Notification notif = Notification.builder()
                        .user(defaulter)
                        .message("Phiên đấu giá '" + auction.getTitle() + "' đã bị hủy do quá hạn thanh toán. Bạn đã mất cọc.")
                        .build();
                notificationRepository.save(notif);
                
                try {
                    emailService.sendHtmlEmail(defaulter.getEmail(), "Cảnh báo: Hủy kết quả đấu giá", "Bạn đã không thanh toán đúng hạn cho phiên đấu giá '" + auction.getTitle() + "'. Tiền cọc của bạn đã bị thu hồi.");
                } catch (Exception e) {
                    log.error("Lỗi khi gửi email thông báo hủy đấu giá: ", e);
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuctionDto> getMyWonAuctions(Long userId) {
        return auctionRepository.findByWinnerIdAndStatusInOrderByIdDesc(
                userId, 
                Arrays.asList(AuctionStatus.SOLD, AuctionStatus.PAID, AuctionStatus.SHIPPED)
        ).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void payWonAuction(Long userId, Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + auctionId));

        if (auction.getWinner() == null || !auction.getWinner().getId().equals(userId)) {
            throw new BadRequestException("Bạn không phải là người chiến thắng phiên đấu giá này");
        }

        if (auction.getStatus() != AuctionStatus.SOLD) {
            throw new BadRequestException("Phiên đấu giá không ở trạng thái chờ thanh toán");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        double userBalance = user.getBalance() != null ? user.getBalance() : 0.0;
        double winningPrice = auction.getWinningPrice() != null ? auction.getWinningPrice() : 0.0;
        double depositAmount = auction.getDepositAmount() != null ? auction.getDepositAmount() : 0.0;
        double amountToPay = winningPrice - depositAmount;

        if (userBalance < amountToPay) {
            throw new AppException("Số dư không đủ, vui lòng nạp thêm tiền");
        }

        // Trừ tiền người mua (chỉ trừ phần còn lại sau khi đã trừ cọc)
        user.setBalance(userBalance - amountToPay);
        userRepository.save(user);

        // Cộng tiền người bán
        User seller = auction.getSeller();
        double sellerBalance = seller.getBalance() != null ? seller.getBalance() : 0.0;
        seller.setBalance(sellerBalance + winningPrice);
        userRepository.save(seller);

        // Cập nhật trạng thái
        auction.setStatus(AuctionStatus.PAID);
        auctionRepository.save(auction);
        
        // Cập nhật trạng thái Order
        orderRepository.findByAuctionId(auctionId).ifPresent(order -> {
            order.setStatus(OrderStatus.PREPARING);
            orderRepository.save(order);
        });

        // Lưu lịch sử giao dịch
        Transaction transaction = Transaction.builder()
                .user(user)
                .auction(auction)
                .amount(winningPrice)
                .type(TransactionType.PAYMENT)
                .status(TransactionStatus.SUCCESS)
                .build();
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuctionDto> searchPublicAuctions(String keyword, Long categoryId, Double minPrice, Double maxPrice, AuctionStatus status, Pageable pageable) {
        Page<Auction> auctionPage = auctionRepository.searchAuctions(keyword, categoryId, minPrice, maxPrice, status, pageable);
        return auctionPage.map(this::mapToDto);
    }

    private AuctionDto mapToDto(Auction auction) {
        return AuctionDto.builder()
                .id(auction.getId())
                .title(auction.getTitle())
                .thumbnail(auction.getThumbnail())
                .description(auction.getDescription())
                .startPrice(auction.getStartPrice())
                .currentPrice(auction.getCurrentPrice())
                .stepPrice(auction.getStepPrice())
                .depositAmount(auction.getDepositAmount())
                .sellerId(auction.getSeller().getId())
                .sellerName(auction.getSeller().getUsername())
                .categoryId(auction.getCategory().getId())
                .categoryName(auction.getCategory().getName())
                .locationId(auction.getLocation().getId())
                .locationName(auction.getLocation().getCityName())
                .status(auction.getStatus())
                .regStartTime(auction.getRegStartTime())
                .regEndTime(auction.getRegEndTime())
                .bidStartTime(auction.getBidStartTime())
                .bidEndTime(auction.getBidEndTime())
                .build();
    }
}
