package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.dto.ContactMessageDto;
import com.example.daugiaonline.application.dto.ContactMessageRequest;
import com.example.daugiaonline.application.service.ContactMessageService;
import com.example.daugiaonline.entity.ContactMessage;
import com.example.daugiaonline.enums.ContactStatus;
import com.example.daugiaonline.infrastructure.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactMessageServiceImpl implements ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;

    @Override
    @Transactional
    public void createMessage(ContactMessageRequest request) {
        contactMessageRepository.save(ContactMessage.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .subject(request.getSubject())
                .message(request.getMessage())
                .status(ContactStatus.UNREAD)
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactMessageDto> getAllMessages() {
        return contactMessageRepository.findAll().stream()
                .map(message -> ContactMessageDto.builder()
                        .id(message.getId())
                        .fullName(message.getFullName())
                        .email(message.getEmail())
                        .subject(message.getSubject())
                        .message(message.getMessage())
                        .status(message.getStatus())
                        .createdAt(message.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        contactMessageRepository.findById(id).ifPresent(message -> {
            message.setStatus(ContactStatus.READ);
            contactMessageRepository.save(message);
        });
    }
}
