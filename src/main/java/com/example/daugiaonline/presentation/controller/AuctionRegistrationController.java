package com.example.daugiaonline.presentation.controller;

import com.example.daugiaonline.application.dto.AuctionRegistrationDto;
import com.example.daugiaonline.application.dto.AuctionRegistrationRequest;
import com.example.daugiaonline.application.service.AuctionRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auction-registrations")
@RequiredArgsConstructor
public class AuctionRegistrationController {

    private final AuctionRegistrationService registrationService;

    @PostMapping
    public ResponseEntity<AuctionRegistrationDto> registerForAuction(@Valid @RequestBody AuctionRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registrationService.registerForAuction(request));
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<AuctionRegistrationDto>> getRegistrationsByAuction(@PathVariable Long auctionId) {
        return ResponseEntity.ok(registrationService.getRegistrationsByAuction(auctionId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuctionRegistrationDto>> getRegistrationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(registrationService.getRegistrationsByUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<AuctionRegistrationDto>> getAllRegistrations() {
        return ResponseEntity.ok(registrationService.getAllRegistrations());
    }
}
