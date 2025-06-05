package com.tablehub.thbackend.controller;

import com.tablehub.thbackend.dto.response.RestaurantResponseDto;
import com.tablehub.thbackend.dto.response.RestaurantsResponse;
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
    public ResponseEntity<RestaurantsResponse> getAllRestaurants() {
        RestaurantsResponse response = restaurantDataService.getAllRestaurants();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<RestaurantResponseDto>> getAllRestaurantsAsList() {
        try {
            List<RestaurantResponseDto> restaurants = restaurantDataService.getAllRestaurantsAsList();
            return ResponseEntity.ok(restaurants);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> getRestaurantById(@PathVariable Long id) {
        try {
            RestaurantResponseDto restaurant = restaurantDataService.getRestaurantById(id);
            return ResponseEntity.ok(restaurant);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
