package com.tablehub.thbackend.controller;

import com.tablehub.thbackend.dto.RestaurantDTO;
import com.tablehub.thbackend.model.Restaurant;
import com.tablehub.thbackend.service.interfaces.RestaurantDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "*")
public class RestaurantController {

    private final RestaurantDataService restaurantDataService;

    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantDataService.getAllRestaurants();
        return ResponseEntity.ok(restaurants.stream().map(RestaurantDTO::new).toList());
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
//        return restaurantDataService.getRestaurantById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
}