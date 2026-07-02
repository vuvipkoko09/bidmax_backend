package com.example.daugiaonline.infrastructure.repository;

import com.example.daugiaonline.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByAuctionIdOrderByBidAmountDesc(Long auctionId);
    List<Bid> findByUserIdOrderByBidTimeDesc(Long userId);
}
