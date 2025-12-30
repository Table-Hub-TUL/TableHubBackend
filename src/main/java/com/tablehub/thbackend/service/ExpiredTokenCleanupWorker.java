package com.tablehub.thbackend.service;

import com.tablehub.thbackend.repo.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ExpiredTokenCleanupWorker {
    private final PasswordResetTokenRepository tokenRepository;

    @Transactional
    @Scheduled(cron = "0 0 * * * *") // every hour
    public void deleteExpiredTokens() {
        tokenRepository.deleteAllExpiredSince(LocalDateTime.now());
    }
}
