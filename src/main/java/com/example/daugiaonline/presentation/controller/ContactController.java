package com.example.daugiaonline.presentation.controller;

import com.example.daugiaonline.application.dto.ContactMessageDto;
import com.example.daugiaonline.application.dto.ContactMessageRequest;
import com.example.daugiaonline.application.service.ContactMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ContactController {

    private final ContactMessageService contactMessageService;

    @PostMapping("/public/contact")
    public ResponseEntity<Void> createMessage(@RequestBody ContactMessageRequest request) {
        contactMessageService.createMessage(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/contact")
    public ResponseEntity<List<ContactMessageDto>> getAllMessages() {
        return ResponseEntity.ok(contactMessageService.getAllMessages());
    }

    @PutMapping("/admin/contact/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        contactMessageService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
