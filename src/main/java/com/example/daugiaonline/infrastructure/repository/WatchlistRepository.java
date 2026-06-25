package com.example.daugiaonline.infrastructure.repository;

import com.example.daugiaonline.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    List<Watchlist> findByUserIdOrderByAddedAtDesc(Long userId);
    Optional<Watchlist> findByUserIdAndAuctionId(Long userId, Long auctionId);
}
