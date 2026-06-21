package com.meowtfit.backend.common.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
