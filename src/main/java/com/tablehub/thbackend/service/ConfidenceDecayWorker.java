package com.tablehub.thbackend.service;

import com.tablehub.thbackend.model.TableStatus;
import com.tablehub.thbackend.repo.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// TODO: make this more intelligent later on
@Component
@RequiredArgsConstructor
public class ConfidenceDecayWorker {

    private static final Logger logger = LoggerFactory.getLogger(ConfidenceDecayWorker.class);
    private final RestaurantTableRepository tableRepository;

    private static final int DECAY_AMOUNT = 10;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void decayConfidence() {
        logger.debug("Starting confidence decay job...");

        tableRepository.decrementConfidenceForActiveTables(DECAY_AMOUNT, TableStatus.UNKNOWN);
        tableRepository.resetExpiredTables(TableStatus.UNKNOWN);

        logger.debug("Confidence decay job completed.");
    }
}