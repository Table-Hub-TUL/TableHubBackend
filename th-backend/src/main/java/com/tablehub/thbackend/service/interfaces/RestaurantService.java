package com.tablehub.thbackend.service.interfaces;

import com.tablehub.thbackend.dto.*;

import java.util.List;

public interface RestaurantService {

    List<RestaurantStatusDto> getAllRestaurantStatuses();

    RestaurantStatusDto getStatusFor(Long restaurantId);

    UpdateTableStatusResponse updateTableStatus(UpdateTableStatusRequest request, String username);
}
