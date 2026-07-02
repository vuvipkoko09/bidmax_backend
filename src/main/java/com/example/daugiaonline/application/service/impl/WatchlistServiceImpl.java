package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.dto.WatchlistDto;
import com.example.daugiaonline.application.dto.WatchlistRequest;
import com.example.daugiaonline.application.service.WatchlistService;
import com.example.daugiaonline.entity.Auction;
import com.example.daugiaonline.entity.User;
import com.example.daugiaonline.entity.Watchlist;
import com.example.daugiaonline.exception.BadRequestException;
import com.example.daugiaonline.exception.ResourceNotFoundException;
import com.example.daugiaonline.infrastructure.repository.AuctionRepository;
import com.example.daugiaonline.infrastructure.repository.UserRepository;
import com.example.daugiaonline.infrastructure.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WatchlistServiceImpl implements WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;

    @Override
    @Transactional
    public WatchlistDto addToWatchlist(WatchlistRequest request) {
        if (watchlistRepository.findByUserIdAndAuctionId(request.getUserId(), request.getAuctionId()).isPresent()) {
            throw new BadRequestException("Auction is already in the watchlist.");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Auction auction = auctionRepository.findById(request.getAuctionId())
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + request.getAuctionId()));

        Watchlist watchlist = Watchlist.builder()
                .user(user)
                .auction(auction)
                .build();

        Watchlist saved = watchlistRepository.save(watchlist);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void removeFromWatchlist(Long id) {
        if (!watchlistRepository.existsById(id)) {
            throw new ResourceNotFoundException("Watchlist item not found with id: " + id);
        }
        watchlistRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WatchlistDto> getUserWatchlist(Long userId) {
        return watchlistRepository.findByUserIdOrderByAddedAtDesc(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WatchlistDto> getAllWatchlists() {
        return watchlistRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private WatchlistDto mapToDto(Watchlist watchlist) {
        Auction auction = watchlist.getAuction();
        String thumbnail = auction.getThumbnail();

        return WatchlistDto.builder()
                .id(watchlist.getId())
                .userId(watchlist.getUser().getId())
                .auctionId(auction.getId())
                .auctionTitle(auction.getTitle())
                .currentPrice(auction.getCurrentPrice())
                .status(auction.getStatus().name())
                .thumbnail(thumbnail)
                .addedAt(watchlist.getAddedAt())
                .build();
    }
}
