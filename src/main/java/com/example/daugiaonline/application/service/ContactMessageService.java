package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.ContactMessageDto;
import com.example.daugiaonline.application.dto.ContactMessageRequest;

import java.util.List;

public interface ContactMessageService {
    void createMessage(ContactMessageRequest request);
    List<ContactMessageDto> getAllMessages();
    void markAsRead(Long id);
}
