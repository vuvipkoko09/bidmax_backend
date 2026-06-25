package com.example.daugiaonline.presentation.controller;

import com.example.daugiaonline.application.dto.AuctionImageDto;
import com.example.daugiaonline.application.service.AuctionImageService;
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
@RequestMapping("/api/v1/auction-images")
@RequiredArgsConstructor
public class AuctionImageController {

    private final AuctionImageService auctionImageService;

    @PostMapping
    public ResponseEntity<AuctionImageDto> addImage(@Valid @RequestBody AuctionImageDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionImageService.addImage(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeImage(@PathVariable Long id) {
        auctionImageService.removeImage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<AuctionImageDto>> getAuctionImages(@PathVariable Long auctionId) {
        return ResponseEntity.ok(auctionImageService.getAuctionImages(auctionId));
    }

    @GetMapping
    public ResponseEntity<List<AuctionImageDto>> getAllImages() {
        return ResponseEntity.ok(auctionImageService.getAllImages());
    }
}
