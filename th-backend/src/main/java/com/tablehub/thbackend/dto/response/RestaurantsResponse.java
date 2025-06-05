package com.tablehub.thbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class RestaurantsResponse {
    private List<RestaurantResponseDto> restaurants;
}
