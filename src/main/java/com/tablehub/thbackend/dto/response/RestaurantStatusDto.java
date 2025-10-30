package com.tablehub.thbackend.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class RestaurantStatusDto {
    private Long restaurantId;
    private String name;
    private int freeTableCount;
    private int totalTableCount;
    private Instant timestamp;

}
