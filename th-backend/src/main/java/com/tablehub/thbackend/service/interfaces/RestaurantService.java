package com.tablehub.thbackend.service.interfaces;

import com.tablehub.thbackend.dto.request.UpdateTableStatusRequest;
import com.tablehub.thbackend.dto.response.RestaurantStatusResponse;
import com.tablehub.thbackend.dto.response.UpdateTableStatusResponse;

import java.util.List;

public interface RestaurantService {

    List<RestaurantStatusResponse> getAllRestaurantStatuses();

    RestaurantStatusResponse getStatusFor(Long restaurantId);

    UpdateTableStatusResponse updateTableStatus(UpdateTableStatusRequest request, String username);
}
