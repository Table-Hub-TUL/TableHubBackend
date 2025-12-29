package com.tablehub.thbackend.service.interfaces;

public interface MailingService {
    void sendResetPasswordEmail(String to, String token);
}
