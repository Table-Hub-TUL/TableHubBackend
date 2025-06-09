package com.tablehub.thbackend.dto.response;

import com.tablehub.thbackend.dto.RestaurantSubscriptionInitialState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Data
@RequiredArgsConstructor
public class RestaurantSubscriptionResponse {
    private Long restaurantId;
    private boolean success;
    private String message;
    private RestaurantSubscriptionInitialState initialState;

}
