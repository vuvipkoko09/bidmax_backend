package com.example.daugiaonline.application.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.example.daugiaonline.application.dto.UpdateProfileRequest;
import com.example.daugiaonline.application.dto.UserProfileResponse;
import com.example.daugiaonline.application.service.UserService;
import com.example.daugiaonline.entity.User;
import com.example.daugiaonline.enums.UserStatus;
import com.example.daugiaonline.exception.ResourceNotFoundException;
import com.example.daugiaonline.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToProfileResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return mapToProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        User updatedUser = userRepository.save(user);

        return mapToProfileResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserProfileResponse topUpBalance(Long userId, Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Top up amount must be positive");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        double currentBalance = user.getBalance() != null ? user.getBalance() : 0.0;
        user.setBalance(currentBalance + amount);
        User updatedUser = userRepository.save(user);

        return mapToProfileResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setStatus(status);
        User updatedUser = userRepository.save(user);
        return mapToProfileResponse(updatedUser);
    }

    private UserProfileResponse mapToProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .balance(user.getBalance())
                .roleName(user.getRole() != null ? user.getRole().getRoleName() : null)
                .status(user.getStatus() != null ? user.getStatus() : UserStatus.ACTIVE)
                .build();
    }
}
