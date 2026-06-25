package com.example.daugiaonline.application.service;

public interface EmailService {
    void sendHtmlEmail(String to, String subject, String htmlBody);
}
