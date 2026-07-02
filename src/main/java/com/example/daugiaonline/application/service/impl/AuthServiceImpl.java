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

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.daugiaonline.application.dto.ForgotPasswordRequest;
import com.example.daugiaonline.application.dto.ResetPasswordRequest;
import com.example.daugiaonline.application.service.EmailService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

        String hashedPassword = passwordEncoder.encode(request.getPassword());

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

        System.out.println("DEBUG - Fetched password from DB for user " + user.getUsername() + ": " + user.getPassword());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
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

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setResetToken(otp);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        String htmlBody = "<html><body>"
                + "<h2>Yêu cầu đặt lại mật khẩu</h2>"
                + "<p>Chào bạn,</p>"
                + "<p>Mã xác thực (OTP) đặt lại mật khẩu của bạn là: <b>" + otp + "</b></p>"
                + "<p>Mã này có hiệu lực trong vòng 10 phút. Nếu bạn không yêu cầu, vui lòng bỏ qua email này.</p>"
                + "</body></html>";

        emailService.sendHtmlEmail(user.getEmail(), "Mã xác thực đặt lại mật khẩu", htmlBody);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        if (user.getResetToken() == null || !user.getResetToken().equals(request.getOtp())) {
            throw new BadRequestException("Mã OTP không hợp lệ");
        }

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Mã OTP đã hết hạn");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}
