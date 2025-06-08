package com.tablehub.thbackend.dto.response;

import com.tablehub.thbackend.dto.RestaurantSubscriptionInitialState;

public class RestaurantSubscriptionResponse {
    private Long restaurantId;
    private boolean success;
    private String message;
    private RestaurantSubscriptionInitialState initialState;
}
