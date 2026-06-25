package com.example.daugiaonline.presentation.controller;

import com.example.daugiaonline.application.dto.UpdateProfileRequest;
import com.example.daugiaonline.application.dto.UserProfileResponse;
import com.example.daugiaonline.application.service.UserService;
import com.example.daugiaonline.enums.UserStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        try {
            List<UserProfileResponse> responses = userService.getAllUsers();
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            // Ép Spring Boot phải in dòng chữ đỏ này ra Terminal
            System.err.println("===== LỖI KHI LẤY DANH SÁCH USER =====");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long id) {
        UserProfileResponse response = userService.getUserProfile(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse response = userService.updateProfile(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/topup")
    public ResponseEntity<UserProfileResponse> topUpBalance(
            @PathVariable Long id,
            @RequestParam("amount") Double amount) {
        UserProfileResponse response = userService.topUpBalance(id, amount);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<UserProfileResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        UserStatus status = UserStatus.valueOf(body.get("status"));
        UserProfileResponse response = userService.updateUserStatus(id, status);
        return ResponseEntity.ok(response);
    }
}
