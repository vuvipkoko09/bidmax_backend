package com.example.daugiaonline.infrastructure.repository;

import com.example.daugiaonline.entity.AuctionDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionDepositRepository extends JpaRepository<AuctionDeposit, Long> {
    List<AuctionDeposit> findByAuctionId(Long auctionId);
    List<AuctionDeposit> findByUserId(Long userId);
    Optional<AuctionDeposit> findByAuctionIdAndUserId(Long auctionId, Long userId);
}
