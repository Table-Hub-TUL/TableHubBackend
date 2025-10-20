package com.tablehub.thbackend.dto.request;


import com.tablehub.thbackend.dto.types.SectionDto;
import com.tablehub.thbackend.model.CuisineName;
import com.tablehub.thbackend.model.Location;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Filter criteria for searching restaurants")
public class RestaurantFilterRequest {
    @Parameter(description = "List of cuisine types to filter by")
    @Schema(description = "Cuisine types (e.g., ITALIAN, JAPANESE, MEXICAN)", example = "ITALIAN,JAPANESE")
    private List<CuisineName> cuisine;

    @Parameter(description = "Minimum rating filter")
    @Schema(description = "Minimum restaurant rating", example = "4.5", minimum = "0", maximum = "5")
    private Double rating;

    @Parameter(description = "User's current latitude for distance-based search")
    @Schema(description = "Latitude coordinate (WGS 84)", example = "52.2297")
    private Double userLat;

    @Parameter(description = "User's current longitude for distance-based search")
    @Schema(description = "Longitude coordinate (WGS 84)", example = "21.0122")
    private Double userLon;

    @Parameter(description = "Search radius in kilometers")
    @Schema(description = "Radius for distance search in kilometers", example = "5.0", minimum = "0")
    private Double radius;

    @Parameter(description = "Maximum number of restaurants to return")
    @Schema(description = "Maximum number of results", example = "10", minimum = "1")
    private int restaurantAmount; // Optional
}
