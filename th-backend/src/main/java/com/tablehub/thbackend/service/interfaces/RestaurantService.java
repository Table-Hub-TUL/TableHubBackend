package com.tablehub.thbackend.service.interfaces;

import com.tablehub.thbackend.dto.request.UpdateTableStatusRequest;
import com.tablehub.thbackend.dto.response.RestaurantStatusDto;
import com.tablehub.thbackend.dto.response.UpdateTableStatusResponse;
import com.tablehub.thbackend.dto.websocket.TableUpdateEvent;

import java.util.List;

public interface RestaurantService {

    List<RestaurantStatusDto> getAllRestaurantStatuses();

    RestaurantStatusDto getStatusFor(Long restaurantId);

    UpdateTableStatusResponse updateTableStatus(TableUpdateEvent request);
}
