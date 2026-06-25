package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.dto.OrderDetailResponse;
import com.example.daugiaonline.application.service.OrderService;
import com.example.daugiaonline.entity.Order;
import com.example.daugiaonline.exception.ResourceNotFoundException;
import com.example.daugiaonline.infrastructure.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDetailResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrderDetailResponse mapToResponse(Order order) {
        return OrderDetailResponse.builder()
                .orderId(order.getId())
                .auctionTitle(order.getAuction() != null ? order.getAuction().getTitle() : null)
                .winnerName(order.getWinner() != null ? order.getWinner().getUsername() : null)
                .sellerName(order.getSeller() != null ? order.getSeller().getUsername() : null)
                .shippingAddress(order.getShippingAddress())
                .trackingCode(order.getTrackingCode())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .build();
    }
}
