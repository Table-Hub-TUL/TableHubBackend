package com.tablehub.thbackend.config;


import com.tablehub.thbackend.dto.RestaurantStatusDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Configuration
public class MockRestaurantData {

    @Bean
    public List<RestaurantStatusDto> restaurants() {
        return LongStream.rangeClosed(1, 10)
                .mapToObj(id -> {
                    int total = ThreadLocalRandom.current().nextInt(5, 21);
                    RestaurantStatusDto dto = new RestaurantStatusDto();
                    dto.setRestaurantId(id);
                    dto.setName("Restauracja " + id);
                    dto.setTotalTableCount(total);
                    dto.setFreeTableCount(total);   // all free for starters
                    dto.setTimestamp(Instant.now());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
