package com.tablehub.thbackend.service.interfaces;

import com.tablehub.thbackend.dto.request.RestaurantFilterRequest;
import com.tablehub.thbackend.model.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantDataService {
    List<Restaurant> getAllRestaurants();
    Optional<Restaurant> getRestaurantById(Long id);

    List<Restaurant> findRestaurantsByCriteria(RestaurantFilterRequest criteria);
}