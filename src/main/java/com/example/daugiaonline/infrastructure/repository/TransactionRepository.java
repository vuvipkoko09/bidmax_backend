package com.example.daugiaonline.infrastructure.repository;

import com.example.daugiaonline.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Transaction> findByAuctionIdOrderByCreatedAtDesc(Long auctionId);
    java.util.Optional<Transaction> findByVnpayTranId(String vnpayTranId);
}
