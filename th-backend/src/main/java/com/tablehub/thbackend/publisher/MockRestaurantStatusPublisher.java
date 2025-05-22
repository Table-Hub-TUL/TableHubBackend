package com.tablehub.thbackend.publisher;

import com.tablehub.thbackend.dto.RestaurantStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class MockRestaurantStatusPublisher {

    private final SimpMessagingTemplate template;
    private final List<RestaurantStatusDto> restaurants;
    private final AtomicInteger counter = new AtomicInteger(0);

    @Scheduled(fixedRate = 6000, initialDelay = 0)
    public void publishUpdate() {
        int run = counter.incrementAndGet();
        if (run > 5) return;

        int idx = ThreadLocalRandom.current().nextInt(restaurants.size());
        RestaurantStatusDto dto = restaurants.get(idx);

        int total = dto.getTotalTableCount();
        int free  = dto.getFreeTableCount();
        int delta = ThreadLocalRandom.current().nextInt(-2, 3);
        int newFree = Math.max(0, Math.min(total, free + delta));

        dto.setFreeTableCount(newFree);
        dto.setTimestamp(Instant.now());

        Map<String, Object> header = Map.of(
                "messageId", UUID.randomUUID().toString(),
                "correlationId", null,
                "sender", "server",
                "type", "TABLE_STATUS_CHANGED_EVENT",
                "accessToken", null,
                "timestamp", Instant.now().toEpochMilli()
        );

        Map<String, Object> envelope = Map.of(
                "header", header,
                "body", Map.of(
                        "restaurantId", dto.getRestaurantId(),
                        "name",          dto.getName(),
                        "freeTableCount", dto.getFreeTableCount(),
                        "totalTableCount", dto.getTotalTableCount(),
                        "timestamp",     dto.getTimestamp().toEpochMilli()
                )
        );

        template.convertAndSend("/topic/restaurant/status", envelope);
    }
}
