package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.WatchlistDto;
import com.example.daugiaonline.application.dto.WatchlistRequest;

import java.util.List;

public interface WatchlistService {
    WatchlistDto addToWatchlist(WatchlistRequest request);
    void removeFromWatchlist(Long id);
    List<WatchlistDto> getUserWatchlist(Long userId);
    List<WatchlistDto> getAllWatchlists();
}
