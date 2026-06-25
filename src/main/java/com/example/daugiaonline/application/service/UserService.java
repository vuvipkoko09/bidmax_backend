package com.example.daugiaonline.application.service;

import java.util.List;

import com.example.daugiaonline.application.dto.UpdateProfileRequest;
import com.example.daugiaonline.application.dto.UserProfileResponse;
import com.example.daugiaonline.enums.UserStatus;

public interface UserService {
    List<UserProfileResponse> getAllUsers();
    UserProfileResponse getUserProfile(Long userId);
    UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request);
    UserProfileResponse topUpBalance(Long userId, Double amount);
    UserProfileResponse updateUserStatus(Long userId, UserStatus status);
}

