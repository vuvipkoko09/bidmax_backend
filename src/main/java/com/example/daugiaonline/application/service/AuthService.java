package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.AuthRequest;
import com.example.daugiaonline.application.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
    String login(AuthRequest request);
}
