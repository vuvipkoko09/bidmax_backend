package com.example.daugiaonline.infrastructure.repository;

import com.example.daugiaonline.entity.Auction;
import com.example.daugiaonline.enums.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Auction a WHERE a.id = :id")
    java.util.Optional<Auction> findByIdWithLock(@Param("id") Long id);

    List<Auction> findByStatusAndBidEndTimeBefore(AuctionStatus status, LocalDateTime currentTime);
    
    @Query("SELECT a FROM Auction a WHERE a.status = :status AND a.bidEndTime < :cutoffTime")
    List<Auction> findUnpaidAuctions(@Param("status") AuctionStatus status, @Param("cutoffTime") LocalDateTime cutoffTime);

    List<Auction> findByStatusOrderByIdDesc(AuctionStatus status);
    List<Auction> findBySellerIdOrderByIdDesc(Long sellerId);
    List<Auction> findByWinnerIdAndStatusInOrderByIdDesc(Long winnerId, List<AuctionStatus> statuses);

    @Query("SELECT COUNT(a) FROM Auction a WHERE a.createdAt >= :startDate AND a.createdAt <= :endDate")
    long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(a) FROM Auction a WHERE a.status = :status AND a.createdAt >= :startDate AND a.createdAt <= :endDate")
    long countByStatusAndCreatedAtBetween(@Param("status") AuctionStatus status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(a) FROM Auction a WHERE a.status IN :statuses AND a.createdAt >= :startDate AND a.createdAt <= :endDate")
    long countByStatusInAndCreatedAtBetween(@Param("statuses") List<AuctionStatus> statuses, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(COALESCE(a.winningPrice, a.currentPrice, a.startPrice)) FROM Auction a WHERE a.status IN :statuses AND a.createdAt >= :startDate AND a.createdAt <= :endDate")
    Double sumRevenueByStatusInAndCreatedAtBetween(@Param("statuses") List<AuctionStatus> statuses, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM Auction a WHERE " +
           "(:keyword IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:categoryId IS NULL OR a.category.id = :categoryId) AND " + 
           "(:minPrice IS NULL OR COALESCE(a.currentPrice, a.startPrice) >= :minPrice) AND " +
           "(:maxPrice IS NULL OR COALESCE(a.currentPrice, a.startPrice) <= :maxPrice) AND " +
           "(:status IS NULL OR a.status = :status)")
    Page<Auction> searchAuctions(@Param("keyword") String keyword,
                                 @Param("categoryId") Long categoryId,
                                 @Param("minPrice") Double minPrice,
                                 @Param("maxPrice") Double maxPrice,
                                 @Param("status") AuctionStatus status,
                                 Pageable pageable);
}