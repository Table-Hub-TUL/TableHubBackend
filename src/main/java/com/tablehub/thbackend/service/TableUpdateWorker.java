package com.tablehub.thbackend.service; // Or service.implementations

import com.tablehub.thbackend.config.RabbitConfig;
import com.tablehub.thbackend.dto.internal.TableUpdateJob;
import com.tablehub.thbackend.dto.response.RestaurantStatusDto;
import com.tablehub.thbackend.model.Restaurant;
import com.tablehub.thbackend.model.TableStatus;
import com.tablehub.thbackend.repo.RestaurantRepository;
import com.tablehub.thbackend.repo.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Configuration
@EnableScheduling
public class TableUpdateWorker {

    private static final Logger logger = LoggerFactory.getLogger(TableUpdateWorker.class);
    private static final String BROADCAST_TOPIC = "/topic/restaurant-aggregates";

    private final RestaurantTableRepository tableRepo;
    private final RestaurantRepository restaurantRepo;
    private final SimpMessagingTemplate messagingTemplate;

    private final Set<Long> restaurantsToUpdate = ConcurrentHashMap.newKeySet();

    /**
     * Listens to the RabbitMQ queue for incoming jobs.
     * Quickly adds the restaurant ID to the Set for batch processing later.
     * @param job The job message containing the restaurant ID.
     */
    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void handleTableUpdate(TableUpdateJob job) {
        if (job != null && job.restaurantId() != null) {
            restaurantsToUpdate.add(job.restaurantId());
            logger.trace("Added restaurant ID: {} to update queue.", job.restaurantId());
        } else {
            logger.warn("Received null job or job with null restaurantId from RabbitMQ queue.");
        }
    }

    /**
     * Periodically processes the unique restaurant IDs collected in the Set.
     * Calculates aggregate counts and broadcasts them.
     */
    @Scheduled(fixedDelay = 750)
    @Transactional(readOnly = true)
    public void processUpdateQueue() {


        if (restaurantsToUpdate.isEmpty()) {
            return;
        }

        Set<Long> batchToProcess = Set.copyOf(restaurantsToUpdate);
        restaurantsToUpdate.removeAll(batchToProcess);

        logger.info("Processing aggregate update batch for {} unique restaurants.", batchToProcess.size());

        for (Long restaurantId : batchToProcess) {
            try {

                Restaurant restaurant = restaurantRepo.findById(restaurantId).orElse(null);

                if (restaurant == null) {
                    logger.warn("Restaurant with ID {} not found during scheduled processing. Skipping aggregate broadcast.", restaurantId);
                    continue; // Process next ID in the batch
                }

                long totalTables = tableRepo.countByRestaurantSectionRestaurantId(restaurantId);
                long freeTables = tableRepo.countByRestaurantSectionRestaurantIdAndStatus(restaurantId, TableStatus.AVAILABLE);

                RestaurantStatusDto aggregateStats = new RestaurantStatusDto(
                        restaurantId,
                        restaurant.getName(),
                        (int) freeTables,
                        (int) totalTables,
                        Instant.now()
                );

                messagingTemplate.convertAndSend(BROADCAST_TOPIC, aggregateStats);
                logger.debug("Broadcasted aggregates for restaurant {}: {}/{} free tables.",
                        restaurant.getName(), freeTables, totalTables);

            } catch (Exception e) {
                logger.error("Failed to process aggregate update for restaurant ID: {}", restaurantId, e);
            }
        }
    }
}