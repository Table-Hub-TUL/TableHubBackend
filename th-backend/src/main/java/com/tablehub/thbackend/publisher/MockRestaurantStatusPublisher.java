package com.tablehub.thbackend.publisher;

import com.tablehub.thbackend.dto.RestaurantResponseDto;
import com.tablehub.thbackend.dto.RestaurantsResponse;
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
    private final List<RestaurantResponseDto> restaurants;
    private final AtomicInteger counter = new AtomicInteger(0);

    @Scheduled(fixedRate = 6000, initialDelay = 0)
    public void publishUpdate() {
        int run = counter.incrementAndGet();
        if (run > 5) return;

        int idx = ThreadLocalRandom.current().nextInt(restaurants.size());
        RestaurantResponseDto restaurant = restaurants.get(idx);

        // rating change simulation
        double newRating = Math.round((restaurant.getRating() +
                ThreadLocalRandom.current().nextDouble(-0.2, 0.2)) * 10) / 10.0;
        newRating = Math.max(1.0, Math.min(5.0, newRating));
        restaurant.setRating(newRating);

        RestaurantsResponse response = new RestaurantsResponse(
                List.of(restaurant)
        );

        template.convertAndSend("/topic/restaurant/updates", response);


        Map<String, Object> header = Map.of(
                "messageId", UUID.randomUUID().toString(),
                "correlationId", null,
                "sender", "server",
                "type", "RESTAURANT_UPDATE_EVENT",
                "accessToken", null,
                "timestamp", Instant.now().toEpochMilli()
        );

        Map<String, Object> body = Map.of(
                "restaurant", Map.of(
                        "id", restaurant.getId(),
                        "name", restaurant.getName(),
                        "address", restaurant.getAddress(),
                        "location", restaurant.getLocation(),
                        "cuisine", restaurant.getCuisine(),
                        "rating", restaurant.getRating()
                )
        );

        Map<String, Object> envelope = Map.of(
                "header", header,
                "body", body
        );

        template.convertAndSend("/topic/restaurant/updates", envelope);
    }
}
