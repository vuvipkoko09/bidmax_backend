package com.example.daugiaonline.infrastructure.repository;

import com.example.daugiaonline.entity.AuctionRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRegistrationRepository extends JpaRepository<AuctionRegistration, Long> {
    List<AuctionRegistration> findByAuctionId(Long auctionId);
    List<AuctionRegistration> findByUserId(Long userId);
    Optional<AuctionRegistration> findByAuctionIdAndUserId(Long auctionId, Long userId);
}
