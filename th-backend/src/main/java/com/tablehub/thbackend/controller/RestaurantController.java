package com.tablehub.thbackend.controller;

import com.tablehub.thbackend.dto.RestaurantDTO;
import com.tablehub.thbackend.model.Restaurant;
import com.tablehub.thbackend.service.interfaces.RestaurantDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "*")
@Tag(name = "Restaurant Management", description = "APIs for managing and retrieving restaurant information")
public class RestaurantController {

    private final RestaurantDataService restaurantDataService;

    @Operation(
            summary = "Get all restaurants",
            description = "Retrieves a list of all restaurants in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list of restaurants",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantDTO.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantDataService.getAllRestaurants();
        return ResponseEntity.ok(restaurants.stream().map(RestaurantDTO::new).toList());
    }
}