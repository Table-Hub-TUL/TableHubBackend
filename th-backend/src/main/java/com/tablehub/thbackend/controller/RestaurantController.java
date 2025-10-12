package com.tablehub.thbackend.controller;

import com.tablehub.thbackend.dto.RestaurantDTO;
import com.tablehub.thbackend.dto.response.RestaurantDetailedResponse;
import com.tablehub.thbackend.dto.response.RestaurantSimpleResponse;
import com.tablehub.thbackend.model.Restaurant;
import com.tablehub.thbackend.service.interfaces.RestaurantDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
                            schema = @Schema(implementation = RestaurantSimpleResponse.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<RestaurantSimpleResponse>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantDataService.getAllRestaurants();
        return ResponseEntity.ok(restaurants.stream().map(RestaurantSimpleResponse::new).toList());
    }

    // TODO: use RestaurantDetailedRequest if needed
    @Operation(
            summary = "Get detailed information about a restaurant by ID",
            description = "Fetches a detailed representation of a restaurant using its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant found",
                    content = @Content(schema = @Schema(implementation = RestaurantDetailedResponse.class))),
            @ApiResponse(responseCode = "404", description = "Restaurant not found",
                    content = @Content)
    })
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDetailedResponse> getRestaurantDetailed(
            @PathVariable Long restaurantId) {

        Optional<Restaurant> restaurant = restaurantDataService.getRestaurantById(restaurantId);

        return restaurant.map(value -> ResponseEntity.ok(new RestaurantDetailedResponse(value))).orElseGet(() -> ResponseEntity.notFound().build());

    }
}