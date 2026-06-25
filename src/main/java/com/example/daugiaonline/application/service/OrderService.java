package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.OrderDetailResponse;

import java.util.List;

public interface OrderService {
    OrderDetailResponse getOrderDetails(Long orderId);
    List<OrderDetailResponse> getAllOrders();
}
