package com.example.daugiaonline.presentation.controller;

import com.example.daugiaonline.application.dto.WatchlistDto;
import com.example.daugiaonline.application.dto.WatchlistRequest;
import com.example.daugiaonline.application.service.WatchlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/watchlists")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @PostMapping
    public ResponseEntity<WatchlistDto> addToWatchlist(@Valid @RequestBody WatchlistRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(watchlistService.addToWatchlist(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromWatchlist(@PathVariable Long id) {
        watchlistService.removeFromWatchlist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WatchlistDto>> getUserWatchlist(@PathVariable Long userId) {
        return ResponseEntity.ok(watchlistService.getUserWatchlist(userId));
    }

    @GetMapping
    public ResponseEntity<List<WatchlistDto>> getAllWatchlists() {
        return ResponseEntity.ok(watchlistService.getAllWatchlists());
    }
}
