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
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedUsers();
        migratePlaintextPasswords();
        seedCategories();
        seedLocations();
        seedAuctions();
        seedAdditionalData();
        log.info("Database seeding completed.");
    }

    private void migratePlaintextPasswords() {
        java.util.List<User> users = userRepository.findAll();
        for (User user : users) {
            // BCrypt hashes start with $2a$, $2b$, or $2y$
            if (user.getPassword() != null && !user.getPassword().startsWith("$2")) {
                log.info("Migrating plaintext password for user: {}", user.getUsername());
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
            }
        }
    }

    private String mockHash(String password) {
        if (password == null) return null;
        return passwordEncoder.encode(password);
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
        log.info("Seeding users...");
        Role adminRole = roleRepository.findByRoleName(RoleName.ADMIN).orElseThrow();
        Role sellerRole = roleRepository.findByRoleName(RoleName.SELLER).orElseThrow();
        Role userRole = roleRepository.findByRoleName(RoleName.USER).orElseThrow();

        upsertUser("vb", "admin123", "admin@bidmax.com", adminRole, 100000000.0, "0987654321", "Hà Nội");
        upsertUser("seller1", "seller123", "seller1@bidmax.com", sellerRole, 0.0, "0123456789", "Hồ Chí Minh");
        upsertUser("seller2", "seller123", "seller2@bidmax.com", sellerRole, 0.0, "0981112222", "Đà Nẵng");
        upsertUser("user1", "user123", "user1@bidmax.com", userRole, 5000000.0, "0111222333", "Đà Nẵng");
        upsertUser("user2", "user123", "user2@bidmax.com", userRole, 15000000.0, "0998887777", "Cần Thơ");
    }

    private void upsertUser(String username, String rawPassword, String email, Role role, Double balance, String phone, String address) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            user = User.builder()
                    .username(username)
                    .email(email)
                    .balance(balance)
                    .phone(phone)
                    .address(address)
                    .role(role)
                    .build();
        }
        // Always reset to a valid BCrypt hash
        user.setPassword(mockHash(rawPassword));
        userRepository.save(user);
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

                Auction auction5 = Auction.builder()
                        .title("Macbook Pro M3 Max 16-inch 2023")
                        .description("Máy tính xách tay cấu hình cao nhất. Mới 100%, bảo hành Apple Care+ 3 năm.")
                        .startPrice(90000000.0)
                        .currentPrice(95000000.0)
                        .stepPrice(1000000.0)
                        .depositAmount(5000000.0)
                        .status(AuctionStatus.ACTIVE)
                        .regStartTime(LocalDateTime.now().minusDays(1))
                        .regEndTime(LocalDateTime.now().plusDays(1))
                        .bidStartTime(LocalDateTime.now().plusDays(2))
                        .bidEndTime(LocalDateTime.now().plusDays(5))
                        .category(categoryRepository.findAll().stream().filter(c -> c.getName().equals("Đồ điện tử")).findFirst().orElse(watch))
                        .location(hn)
                        .seller(seller)
                        .build();

                Auction auction6 = Auction.builder()
                        .title("Túi xách Hermes Birkin 30 Crocodile")
                        .description("Túi xách da cá sấu cao cấp. Màu đen bóng, phụ kiện mạ vàng đính kim cương.")
                        .startPrice(500000000.0)
                        .currentPrice(520000000.0)
                        .stepPrice(10000000.0)
                        .depositAmount(50000000.0)
                        .status(AuctionStatus.ACTIVE)
                        .regStartTime(LocalDateTime.now().minusDays(5))
                        .regEndTime(LocalDateTime.now().minusDays(1))
                        .bidStartTime(LocalDateTime.now())
                        .bidEndTime(LocalDateTime.now().plusDays(3))
                        .category(categoryRepository.findAll().stream().filter(c -> c.getName().equals("Trang sức")).findFirst().orElse(watch))
                        .location(hn)
                        .seller(seller)
                        .build();

                Auction auction7 = Auction.builder()
                        .title("Tượng Phật Ngọc ngọc bích tự nhiên")
                        .description("Tượng Phật Ngọc nguyên khối nặng 5kg. Chạm khắc thủ công tinh xảo.")
                        .startPrice(100000000.0)
                        .currentPrice(105000000.0)
                        .stepPrice(2000000.0)
                        .depositAmount(10000000.0)
                        .status(AuctionStatus.ACTIVE)
                        .regStartTime(LocalDateTime.now().minusDays(3))
                        .regEndTime(LocalDateTime.now().plusDays(1))
                        .bidStartTime(LocalDateTime.now().plusDays(2))
                        .bidEndTime(LocalDateTime.now().plusDays(7))
                        .category(art)
                        .location(hn)
                        .seller(seller)
                        .build();

                Auction auction8 = Auction.builder()
                        .title("Nhẫn Kim Cương tự nhiên 2 Carat")
                        .description("Nhẫn kim cương nước D, độ tinh khiết VVS1. Giấy kiểm định GIA.")
                        .startPrice(300000000.0)
                        .currentPrice(350000000.0)
                        .stepPrice(5000000.0)
                        .depositAmount(30000000.0)
                        .status(AuctionStatus.ACTIVE)
                        .regStartTime(LocalDateTime.now().minusDays(4))
                        .regEndTime(LocalDateTime.now().minusDays(1))
                        .bidStartTime(LocalDateTime.now())
                        .bidEndTime(LocalDateTime.now().plusDays(1))
                        .category(categoryRepository.findAll().stream().filter(c -> c.getName().equals("Trang sức")).findFirst().orElse(watch))
                        .location(hn)
                        .seller(seller)
                        .build();

                auctionRepository.saveAll(Arrays.asList(auction1, auction2, auction3, auction4, auction5, auction6, auction7, auction8));
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

                Auction auction5 = auctionRepository.findAll().stream().filter(a -> a.getTitle().contains("Macbook")).findFirst().orElse(null);
                Auction auction6 = auctionRepository.findAll().stream().filter(a -> a.getTitle().contains("Hermes")).findFirst().orElse(null);
                Auction auction7 = auctionRepository.findAll().stream().filter(a -> a.getTitle().contains("Tượng")).findFirst().orElse(null);
                Auction auction8 = auctionRepository.findAll().stream().filter(a -> a.getTitle().contains("Nhẫn")).findFirst().orElse(null);

                if (admin != null && seller != null && user1 != null && auction1 != null && auction2 != null && auction3 != null && auction4 != null) {
                    
                    // 1. AuctionImage
                    List<AuctionImage> images = new java.util.ArrayList<>(Arrays.asList(
                        AuctionImage.builder().auction(auction1).imageUrl("https://images.unsplash.com/photo-1547996160-81dfa63595aa?auto=format&fit=crop&q=80&w=800").isPrimary(true).build(),
                        AuctionImage.builder().auction(auction2).imageUrl("https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?auto=format&fit=crop&q=80&w=800").isPrimary(true).build(),
                        AuctionImage.builder().auction(auction3).imageUrl("https://images.unsplash.com/photo-1695048133142-1a20484d2569?auto=format&fit=crop&q=80&w=800").isPrimary(true).build(),
                        AuctionImage.builder().auction(auction4).imageUrl("https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?auto=format&fit=crop&q=80&w=800").isPrimary(true).build()
                    ));

                    if (auction5 != null) images.add(AuctionImage.builder().auction(auction5).imageUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&q=80&w=800").isPrimary(true).build());
                    if (auction6 != null) images.add(AuctionImage.builder().auction(auction6).imageUrl("https://images.unsplash.com/photo-1584916201218-f4242ceb4809?auto=format&fit=crop&q=80&w=800").isPrimary(true).build());
                    if (auction7 != null) images.add(AuctionImage.builder().auction(auction7).imageUrl("https://images.unsplash.com/photo-1605806616949-1e87b487cb2a?auto=format&fit=crop&q=80&w=800").isPrimary(true).build());
                    if (auction8 != null) images.add(AuctionImage.builder().auction(auction8).imageUrl("https://images.unsplash.com/photo-1605100804763-247f67b2548e?auto=format&fit=crop&q=80&w=800").isPrimary(true).build());
                    
                    auctionImageRepository.saveAll(images);

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
