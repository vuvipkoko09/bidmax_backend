package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.NotificationDto;
import java.util.List;

public interface NotificationService {
    NotificationDto createNotification(Long userId, String message);
    List<NotificationDto> getUserNotifications(Long userId);
    List<NotificationDto> getAllNotifications();
    void markAsRead(Long id);
}
