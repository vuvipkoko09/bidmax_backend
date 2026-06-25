package com.example.daugiaonline.config;

import com.example.daugiaonline.entity.*;
import com.example.daugiaonline.enums.*;
import com.example.daugiaonline.infrastructure.repository.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final AuctionRepository auctionRepository;
    private final ArticleRepository articleRepository;
    private final ArticleReviewRepository articleReviewRepository;
    private final AuctionDepositRepository auctionDepositRepository;
    private final AuctionImageRepository auctionImageRepository;
    private final AuctionRegistrationRepository auctionRegistrationRepository;
    private final AuditLogRepository auditLogRepository;
    private final BidRepository bidRepository;
    private final NewsRepository newsRepository;
    private final NotificationRepository notificationRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final TransactionRepository transactionRepository;
    private final WatchlistRepository watchlistRepository;

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedUsers();
        seedCategories();
        seedLocations();
        seedAuctions();
        seedAdditionalData();
        log.info("Database seeding completed.");
    }

    private String mockHash(String password) {
        if (password == null) return null;
        return "$2a$10$mockHashed_" + Base64.getEncoder().encodeToString(password.getBytes());
    }

    private void seedRoles() {
        if (roleRepository.count() == 0) {
            log.info("Seeding roles...");
            roleRepository.save(Role.builder().roleName(RoleName.ADMIN).build());
            roleRepository.save(Role.builder().roleName(RoleName.SELLER).build());
            roleRepository.save(Role.builder().roleName(RoleName.USER).build());
        }
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            log.info("Seeding users...");
            Role adminRole = roleRepository.findByRoleName(RoleName.ADMIN).orElseThrow();
            Role sellerRole = roleRepository.findByRoleName(RoleName.SELLER).orElseThrow();
            Role userRole = roleRepository.findByRoleName(RoleName.USER).orElseThrow();

            User admin = User.builder()
                    .username("admin")
                    .password(mockHash("admin123"))
                    .email("admin@bidmax.com")
                    .balance(100000000.0)
                    .phone("0987654321")
                    .address("Hà Nội")
                    .role(adminRole)
                    .build();

            User seller = User.builder()
                    .username("seller1")
                    .password(mockHash("seller123"))
                    .email("seller1@bidmax.com")
                    .balance(0.0)
                    .phone("0123456789")
                    .address("Hồ Chí Minh")
                    .role(sellerRole)
                    .build();

            User user1 = User.builder()
                    .username("user1")
                    .password(mockHash("user123"))
                    .email("user1@bidmax.com")
                    .balance(5000000.0)
                    .phone("0111222333")
                    .address("Đà Nẵng")
                    .role(userRole)
                    .build();

            userRepository.saveAll(Arrays.asList(admin, seller, user1));
        }
    }

    private void seedCategories() {
        if (categoryRepository.count() == 0) {
            log.info("Seeding categories...");
            categoryRepository.saveAll(Arrays.asList(
                Category.builder().name("Đồng hồ").description("Các loại đồng hồ cao cấp").build(),
                Category.builder().name("Nghệ thuật").description("Tranh, tượng nghệ thuật").build(),
                Category.builder().name("Đồ điện tử").description("Điện thoại, máy tính").build(),
                Category.builder().name("Trang sức").description("Vàng, bạc, đá quý").build(),
                Category.builder().name("Đồ cổ").description("Đồ vật có giá trị lịch sử").build()
            ));
        }
    }

    private void seedLocations() {
        if (locationRepository.count() == 0) {
            log.info("Seeding locations...");
            locationRepository.saveAll(Arrays.asList(
                Location.builder().cityName("Hà Nội").address("Trung tâm Hà Nội").build(),
                Location.builder().cityName("Hồ Chí Minh").address("Quận 1, TP.HCM").build()
            ));
        }
    }

    private void seedAuctions() {
        if (auctionRepository.count() == 0) {
            log.info("Seeding auctions...");
            User seller = userRepository.findAll().stream().filter(u -> u.getRole().getRoleName() == RoleName.SELLER).findFirst().orElse(null);
            Category watch = categoryRepository.findAll().stream().filter(c -> c.getName().equals("Đồng hồ")).findFirst().orElse(null);
            Category art = categoryRepository.findAll().stream().filter(c -> c.getName().equals("Nghệ thuật")).findFirst().orElse(null);
            Location hn = locationRepository.findAll().stream().filter(l -> l.getCityName().equals("Hà Nội")).findFirst().orElse(null);

            if (seller != null && watch != null && art != null && hn != null) {
                Auction auction1 = Auction.builder()
                        .title("Đồng hồ Rolex Submariner Date 126610LN")
                        .description("Đồng hồ Rolex chính hãng, fullbox, thẻ bảo hành 2023. Tình trạng hoàn hảo.")
                        .startPrice(300000000.0)
                        .currentPrice(345000000.0)
                        .stepPrice(5000000.0)
                        .depositAmount(30000000.0)
                        .status(AuctionStatus.ACTIVE)
                        .regStartTime(LocalDateTime.now().minusDays(2))
                        .regEndTime(LocalDateTime.now().minusDays(1))
                        .bidStartTime(LocalDateTime.now())
                        .bidEndTime(LocalDateTime.now().plusDays(2))
                        .category(watch)
                        .location(hn)
                        .seller(seller)
                        .build();

                Auction auction2 = Auction.builder()
                        .title("Tranh sơn dầu nghệ thuật 'Mùa Thu Hà Nội'")
                        .description("Tác phẩm độc bản của họa sĩ nổi tiếng. Kích thước 120x80cm.")
                        .startPrice(20000000.0)
                        .currentPrice(28000000.0)
                        .stepPrice(1000000.0)
                        .depositAmount(2000000.0)
                        .status(AuctionStatus.ACTIVE)
                        .regStartTime(LocalDateTime.now().minusDays(2))
                        .regEndTime(LocalDateTime.now().minusDays(1))
                        .bidStartTime(LocalDateTime.now().minusHours(2))
                        .bidEndTime(LocalDateTime.now().plusHours(5))
                        .category(art)
                        .location(hn)
                        .seller(seller)
                        .build();

                Auction auction3 = Auction.builder()
                        .title("Điện thoại iPhone 15 Pro Max 1TB Đã qua sử dụng")
                        .description("iPhone 15 Pro Max màu Titan Tự Nhiên. Pin 99%. Ngoại hình đẹp keng.")
                        .startPrice(20000000.0)
                        .currentPrice(26000000.0)
                        .winningPrice(26000000.0)
                        .stepPrice(500000.0)
                        .depositAmount(2000000.0)
                        .status(AuctionStatus.SOLD) // Chờ thanh toán
                        .regStartTime(LocalDateTime.now().minusDays(10))
                        .regEndTime(LocalDateTime.now().minusDays(8))
                        .bidStartTime(LocalDateTime.now().minusDays(7))
                        .bidEndTime(LocalDateTime.now().minusDays(1))
                        .category(watch) // Reuse category temporarily
                        .location(hn)
                        .seller(seller)
                        .winner(userRepository.findByUsername("user1").orElse(null))
                        .build();

                Auction auction4 = Auction.builder()
                        .title("Tai nghe Sony WH-1000XM5")
                        .description("Tai nghe chống ồn đỉnh cao. Mới nguyên seal.")
                        .startPrice(5000000.0)
                        .currentPrice(6500000.0)
                        .winningPrice(6500000.0)
                        .stepPrice(100000.0)
                        .depositAmount(500000.0)
                        .status(AuctionStatus.PAID) // Đã thanh toán
                        .regStartTime(LocalDateTime.now().minusDays(15))
                        .regEndTime(LocalDateTime.now().minusDays(14))
                        .bidStartTime(LocalDateTime.now().minusDays(13))
                        .bidEndTime(LocalDateTime.now().minusDays(5))
                        .category(watch)
                        .location(hn)
                        .seller(seller)
                        .winner(userRepository.findByUsername("user1").orElse(null))
                        .build();

                auctionRepository.saveAll(Arrays.asList(auction1, auction2, auction3, auction4));
            }
        }
    }

    private void seedAdditionalData() {
        if (auctionImageRepository.count() == 0) {
            log.info("Seeding additional data (Images, Bids, Reviews, etc.)...");
            User admin = userRepository.findAll().stream().filter(u -> u.getRole().getRoleName() == RoleName.ADMIN).findFirst().orElse(null);
            User seller = userRepository.findAll().stream().filter(u -> u.getRole().getRoleName() == RoleName.SELLER).findFirst().orElse(null);
            User user1 = userRepository.findAll().stream().filter(u -> u.getRole().getRoleName() == RoleName.USER).findFirst().orElse(null);
            
                Auction auction1 = auctionRepository.findAll().stream().filter(a -> a.getTitle().contains("Rolex")).findFirst().orElse(null);
                Auction auction2 = auctionRepository.findAll().stream().filter(a -> a.getTitle().contains("Tranh")).findFirst().orElse(null);
                Auction auction3 = auctionRepository.findAll().stream().filter(a -> a.getTitle().contains("iPhone")).findFirst().orElse(null);
                Auction auction4 = auctionRepository.findAll().stream().filter(a -> a.getTitle().contains("Sony")).findFirst().orElse(null);

                if (admin != null && seller != null && user1 != null && auction1 != null && auction2 != null && auction3 != null && auction4 != null) {
                    
                    // 1. AuctionImage
                    AuctionImage img1 = AuctionImage.builder().auction(auction1).imageUrl("https://images.unsplash.com/photo-1547996160-81dfa63595aa?auto=format&fit=crop&q=80&w=800").isPrimary(true).build();
                    AuctionImage img2 = AuctionImage.builder().auction(auction2).imageUrl("https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?auto=format&fit=crop&q=80&w=800").isPrimary(true).build();
                    AuctionImage img3 = AuctionImage.builder().auction(auction3).imageUrl("https://images.unsplash.com/photo-1695048133142-1a20484d2569?auto=format&fit=crop&q=80&w=800").isPrimary(true).build();
                    AuctionImage img4 = AuctionImage.builder().auction(auction4).imageUrl("https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?auto=format&fit=crop&q=80&w=800").isPrimary(true).build();
                    auctionImageRepository.saveAll(Arrays.asList(img1, img2, img3, img4));

                // 2. AuctionRegistration
                AuctionRegistration reg1 = AuctionRegistration.builder().auction(auction1).user(user1).registeredAt(LocalDateTime.now().minusDays(1)).build();
                AuctionRegistration reg2 = AuctionRegistration.builder().auction(auction2).user(user1).registeredAt(LocalDateTime.now().minusDays(1)).build();
                auctionRegistrationRepository.saveAll(Arrays.asList(reg1, reg2));
                
                // 3. AuctionDeposit
                AuctionDeposit dep1 = AuctionDeposit.builder().auction(auction1).user(user1).depositAmount(auction1.getDepositAmount()).status(DepositStatus.LOCKED).createdAt(LocalDateTime.now().minusDays(1)).build();
                AuctionDeposit dep2 = AuctionDeposit.builder().auction(auction2).user(user1).depositAmount(auction2.getDepositAmount()).status(DepositStatus.LOCKED).createdAt(LocalDateTime.now().minusDays(1)).build();
                auctionDepositRepository.saveAll(Arrays.asList(dep1, dep2));

                // 4. Bid
                Bid bid1 = Bid.builder().auction(auction1).user(user1).bidAmount(auction1.getStartPrice() + auction1.getStepPrice()).bidTime(LocalDateTime.now().minusHours(2)).build();
                Bid bid2 = Bid.builder().auction(auction2).user(user1).bidAmount(auction2.getStartPrice() + auction2.getStepPrice()).bidTime(LocalDateTime.now().minusHours(1)).build();
                bidRepository.saveAll(Arrays.asList(bid1, bid2));

                // 5. Transaction
                Transaction t1 = Transaction.builder().user(user1).auction(auction1).amount(auction1.getDepositAmount()).type(TransactionType.PAYMENT).status(TransactionStatus.SUCCESS).vnpayTranId("VNP123456").build();
                transactionRepository.save(t1);

                // 6. Order
                Order order1 = Order.builder().auction(auction2).winner(user1).seller(seller).shippingAddress("Đà Nẵng").trackingCode("VNPOST123").status(OrderStatus.PREPARING).build();
                orderRepository.save(order1);

                // 7. Review
                Review review1 = Review.builder().auction(auction2).reviewer(user1).reviewee(seller).ratingStar(5).comment("Sản phẩm rất đẹp và đúng mô tả. Đóng gói cẩn thận.").build();
                reviewRepository.save(review1);

                // 8. Watchlist
                Watchlist w1 = Watchlist.builder().user(user1).auction(auction1).addedAt(LocalDateTime.now()).build();
                watchlistRepository.save(w1);

                // 9. Notification
                Notification n1 = Notification.builder().user(user1).message("Bạn đã đặt cọc thành công phiên đấu giá Rolex.").isRead(false).createdAt(LocalDateTime.now()).build();
                notificationRepository.save(n1);

                // 10. News
                News news1 = News.builder().title("Luật đấu giá mới nhất 2026").content("Cập nhật quy định mới về đặt cọc và thanh toán...").author(admin).publishedAt(LocalDateTime.now()).build();
                newsRepository.save(news1);

                // 11. Article & ArticleReview
                Article a1 = Article.builder().title("Kinh nghiệm đấu giá đồng hồ Vintage").content("Những điều cần lưu ý khi mua đồng hồ Rolex cổ...").thumbnail("https://images.unsplash.com/photo-1523170335258-f5ed11844a49?auto=format&fit=crop&q=80&w=400").status(ArticleStatus.APPROVED).author(admin).build();
                articleRepository.save(a1);
                
                ArticleReview ar1 = ArticleReview.builder().article(a1).user(user1).rating(5).comment("Bài viết rất hữu ích cho người mới.").build();
                articleReviewRepository.save(ar1);

                // 12. AuditLog
                AuditLog log1 = AuditLog.builder().user(admin).action("CREATE").details("Created Rolex auction").targetTable("AUCTION").targetId(auction1.getId()).oldValue("{}").newValue("{\"message\": \"Created Rolex\"}").timestamp(LocalDateTime.now()).build();
                auditLogRepository.save(log1);
            }
        }
    }
}
