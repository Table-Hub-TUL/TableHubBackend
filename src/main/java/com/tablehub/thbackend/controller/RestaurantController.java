package com.tablehub.thbackend.controller;

import com.tablehub.thbackend.dto.request.RestaurantFilterRequest;
import com.tablehub.thbackend.dto.response.RestaurantDetailedResponse;
import com.tablehub.thbackend.dto.response.RestaurantSimpleResponse;
import com.tablehub.thbackend.model.CuisineName;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "*")
@Tag(name = "Restaurant Management", description = "APIs for managing and retrieving restaurant information")
public class RestaurantController {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

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
    @GetMapping("/all")
    public ResponseEntity<List<RestaurantSimpleResponse>> getAllRestaurants() {
        logger.info("Received request to get all restaurants.");
        List<Restaurant> restaurants = restaurantDataService.getAllRestaurants();
        logger.info("Found {} restaurants. Returning list.", restaurants.size());
        return ResponseEntity.ok(restaurants.stream().map(RestaurantSimpleResponse::new).toList());
    }

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
    @GetMapping()
    public ResponseEntity<List<RestaurantSimpleResponse>> getFilteredRestaurants(
            @ParameterObject RestaurantFilterRequest criteria
    ) {
        logger.info("Received request to filter restaurants (distance = {}).", criteria.getRadius());
        List<Restaurant> restaurants = restaurantDataService.findRestaurantsByCriteria(criteria);

        logger.info("Found {} restaurants matching criteria", restaurants.size());
        return ResponseEntity.ok(restaurants.stream().map(RestaurantSimpleResponse::new).toList());
    }

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
        logger.info("Received request for detailed information for restaurant ID: {}", restaurantId);
        Optional<Restaurant> restaurant = restaurantDataService.getRestaurantById(restaurantId);
        if (restaurant.isPresent()) {
            logger.info("Successfully found restaurant with ID: {}", restaurantId);
            return ResponseEntity.ok(new RestaurantDetailedResponse(restaurant.get()));
        } else {
            logger.warn("Restaurant with ID: {} was not found.", restaurantId);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Get list of all available cuisines",
            description = "Returns a list of all cuisine types available in the system for filtering and categorization purposes"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved cuisine list",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CuisineName.class),
                            examples = @ExampleObject(
                                    value = "[\"ITALIAN\", \"POLISH\", \"MEXICAN\", \"JAPANESE\"]"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error occurred while fetching cuisine list",
                    content = @Content
            )
    })
    @GetMapping("/cuisine-list")
    public ResponseEntity<List<CuisineName>> getCuisineList() {
        logger.info("Received request to get cuisine list");
        try{
            return ResponseEntity.ok(Arrays.asList(CuisineName.values()));
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
}