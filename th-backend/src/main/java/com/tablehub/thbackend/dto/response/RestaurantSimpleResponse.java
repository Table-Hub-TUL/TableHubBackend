package com.tablehub.thbackend.dto.response;


import com.tablehub.thbackend.model.*;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantSimpleResponse {
    private Long id;
    private String name;
    private List<CuisineName> cuisine;
    private Address address;
    private Location location;
    private Double rating = 0.0;

    public  RestaurantSimpleResponse(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.cuisine = List.of(restaurant.getCuisineName());
        this.address = restaurant.getAddress();
        if (restaurant.getLocation() != null) {
            this.location = new Location(restaurant.getLocation().getY(), restaurant.getLocation().getX());
        }
        this.rating = restaurant.getRating();
    }
}
