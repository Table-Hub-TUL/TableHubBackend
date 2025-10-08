package com.tablehub.thbackend.service.interfaces;

import com.tablehub.thbackend.dto.request.UpdateTableStatusRequest;
import com.tablehub.thbackend.dto.response.RestaurantStatusDto;
import com.tablehub.thbackend.dto.response.UpdateTableStatusResponse;

import java.util.List;

public interface RestaurantService {

    List<RestaurantStatusDto> getAllRestaurantStatuses();

    RestaurantStatusDto getStatusFor(Long restaurantId);

    UpdateTableStatusResponse updateTableStatus(UpdateTableStatusRequest request, String username);
}
