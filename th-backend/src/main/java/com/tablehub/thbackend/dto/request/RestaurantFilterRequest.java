package com.tablehub.thbackend.dto.request;


import com.tablehub.thbackend.dto.types.SectionDto;
import com.tablehub.thbackend.model.CuisineName;
import com.tablehub.thbackend.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantFilterRequest {
    private List<CuisineName> cuisine;
    private Double rating;
    private Double userLat;
    private Double userLon;
    private Double radius;
    private int restaurantAmount; //Optional
}
