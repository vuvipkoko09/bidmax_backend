package com.example.daugiaonline.application.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface PaymentService {
    String createPaymentUrl(long amount, String username, HttpServletRequest request);
    void processPaymentReturn(HttpServletRequest request);
}
