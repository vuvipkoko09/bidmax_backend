package com.example.daugiaonline.presentation.controller;

import com.example.daugiaonline.application.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/create-url")
    public ResponseEntity<?> createPaymentUrl(@RequestParam long amount, @RequestParam String username, HttpServletRequest request) {
        String paymentUrl = paymentService.createPaymentUrl(amount, username, request);
        Map<String, String> response = new HashMap<>();
        response.put("url", paymentUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-return")
    public void vnpayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        paymentService.processPaymentReturn(request);
        
        // Tạo query string để truyền sang frontend
        String responseCode = request.getParameter("vnp_ResponseCode");
        String transactionNo = request.getParameter("vnp_TransactionNo");
        
        String frontendUrl = "http://localhost:5173/payment-result" + 
                "?vnp_ResponseCode=" + responseCode + 
                "&vnp_TransactionNo=" + transactionNo;
                
        response.sendRedirect(frontendUrl);
    }
}
