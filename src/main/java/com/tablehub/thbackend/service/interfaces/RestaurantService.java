package com.tablehub.thbackend.service.interfaces;

import com.tablehub.thbackend.dto.response.RestaurantStatusDto;

import java.util.List;

public interface RestaurantService {

    List<RestaurantStatusDto> getAllRestaurantStatuses();

    RestaurantStatusDto getStatusFor(Long restaurantId);
}
