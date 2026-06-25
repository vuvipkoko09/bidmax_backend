package com.example.daugiaonline.presentation.controller;

import com.example.daugiaonline.application.dto.AuctionDepositDto;
import com.example.daugiaonline.application.dto.AuctionDepositRequest;
import com.example.daugiaonline.application.service.AuctionDepositService;
import com.example.daugiaonline.enums.DepositStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auction-deposits")
@RequiredArgsConstructor
public class AuctionDepositController {

    private final AuctionDepositService depositService;

    @PostMapping
    public ResponseEntity<AuctionDepositDto> makeDeposit(@Valid @RequestBody AuctionDepositRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(depositService.makeDeposit(request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateDepositStatus(@PathVariable Long id, @RequestParam DepositStatus status) {
        depositService.updateDepositStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<AuctionDepositDto>> getDepositsByAuction(@PathVariable Long auctionId) {
        return ResponseEntity.ok(depositService.getDepositsByAuction(auctionId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuctionDepositDto>> getDepositsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(depositService.getDepositsByUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<AuctionDepositDto>> getAllDeposits() {
        return ResponseEntity.ok(depositService.getAllDeposits());
    }
}
