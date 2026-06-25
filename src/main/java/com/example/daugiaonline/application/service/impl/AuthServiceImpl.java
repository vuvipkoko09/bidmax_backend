package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.dto.AuthRequest;
import com.example.daugiaonline.application.dto.RegisterRequest;
import com.example.daugiaonline.application.service.AuthService;
import com.example.daugiaonline.entity.Role;
import com.example.daugiaonline.entity.User;
import com.example.daugiaonline.enums.UserStatus;
import com.example.daugiaonline.exception.BadRequestException;
import com.example.daugiaonline.exception.ResourceNotFoundException;
import com.example.daugiaonline.infrastructure.repository.RoleRepository;
import com.example.daugiaonline.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        Role role = roleRepository.findByRoleName(request.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRoleName()));

        String hashedPassword = mockHash(request.getPassword());

        User user = User.builder()
                .username(request.getUsername())
                .password(hashedPassword)
                .email(request.getEmail())
                .role(role)
                .balance(0.0)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public String login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        String hashedInput = mockHash(request.getPassword());
        if (!user.getPassword().equals(hashedInput)) {
            throw new BadRequestException("Invalid username or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            if (user.getStatus() == UserStatus.LOCKED) {
                throw new BadRequestException("Tài khoản của bạn đã bị khóa.");
            } else if (user.getStatus() == UserStatus.BANNED) {
                throw new BadRequestException("Tài khoản của bạn đã bị cấm khỏi hệ thống.");
            } else if (user.getStatus() == UserStatus.PENDING) {
                throw new BadRequestException("Tài khoản đang chờ duyệt.");
            }
        }

        // Mock JWT return for now
        return "mock-jwt-token-for-" + user.getUsername();
    }

    private String mockHash(String password) {
        if (password == null) {
            return "";
        }
        return "$2a$10$mockHashed_" + Base64.getEncoder().encodeToString(password.getBytes());
    }
}
