package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.AuthRequest;
import com.example.daugiaonline.application.dto.ForgotPasswordRequest;
import com.example.daugiaonline.application.dto.RegisterRequest;
import com.example.daugiaonline.application.dto.ResetPasswordRequest;

public interface AuthService {
    void register(RegisterRequest request);
    String login(AuthRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
