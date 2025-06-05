package com.tablehub.thbackend.config;

import com.tablehub.thbackend.model.Location;
import com.tablehub.thbackend.dto.response.RestaurantResponseDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Configuration
public class MockRestaurantData {

//    @Bean
//    public List<RestaurantResponseDto> restaurants() {
//        return LongStream.rangeClosed(1, 10)
//                .mapToObj(id -> {
//                    RestaurantResponseDto dto = new RestaurantResponseDto();
//                    dto.setId(id);
//                    dto.setName("Restauracja " + id);
//                    dto.setAddress("ul. Przykładowa " + id + ", Łódź");
//                    dto.setRating(3.0 + ThreadLocalRandom.current().nextDouble(0, 2.0));  // 3.0 to 5.0
//                    dto.setLocation(new Location(
//                            51.75 + ThreadLocalRandom.current().nextDouble(-0.01, 0.01),  // lat
//                            19.45 + ThreadLocalRandom.current().nextDouble(-0.01, 0.01)   // lng
//                    ));
//                    dto.setCuisine(List.of("Polish", "Italian"));
//                    return dto;
//                })
//                .collect(Collectors.toList());
//    }
}
