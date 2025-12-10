package com.tablehub.thbackend.service;

import com.tablehub.thbackend.model.RestaurantTable;
import com.tablehub.thbackend.model.TableStatus;
import com.tablehub.thbackend.repo.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
// TODO: MAKE THIS BETTER AND MORE INTELLIGENT
@Component
@RequiredArgsConstructor
public class ConfidenceDecayWorker {

    private static final Logger logger = LoggerFactory.getLogger(ConfidenceDecayWorker.class);
    private final RestaurantTableRepository tableRepository;

    private static final int DECAY_AMOUNT = 10; // Drops 10% per minute

    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    @Transactional
    public void decayConfidence() {
        // Fetch all tables that are NOT unknown
        // In a real app, use a custom query: findByStatusNot(UNKNOWN)
        List<RestaurantTable> activeTables = tableRepository.findAll().stream()
                .filter(t -> t.getStatus() != TableStatus.UNKNOWN)
                .toList();

        for (RestaurantTable table : activeTables) {
            int newScore = table.getConfidenceScore() - DECAY_AMOUNT;

            if (newScore <= 0) {
                // Confidence lost, reset status
                table.setConfidenceScore(0);
                table.setStatus(TableStatus.UNKNOWN);
                logger.info("Table {} confidence dropped to 0. Status reset to UNKNOWN.", table.getId());
            } else {
                table.setConfidenceScore(newScore);
            }
        }
        tableRepository.saveAll(activeTables);
    }
}