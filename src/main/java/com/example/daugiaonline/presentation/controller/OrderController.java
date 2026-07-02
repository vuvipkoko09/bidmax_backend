package com.example.daugiaonline.presentation.controller;

import com.example.daugiaonline.application.dto.OrderDetailResponse;
import com.example.daugiaonline.application.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> getOrderDetails(@PathVariable Long id) {
        OrderDetailResponse response = orderService.getOrderDetails(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderDetailResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @org.springframework.web.bind.annotation.PutMapping("/{id}/status")
    public ResponseEntity<OrderDetailResponse> updateOrderStatus(
            @PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestParam com.example.daugiaonline.enums.OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }
}
