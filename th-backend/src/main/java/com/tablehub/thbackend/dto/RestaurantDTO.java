package com.tablehub.thbackend.dto;

import com.tablehub.thbackend.model.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.util.List;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RestaurantDTO {
    @Id
    private Long id;
    private String name;
    private List<CuisineName> cuisine;
    private Address address;
    private Location location;
    private Double rating = 0.0;
    private List<RestaurantSection> sections;

    public RestaurantDTO(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.cuisine = List.of(restaurant.getCuisineName());
        this.address = restaurant.getAddress();
        if (restaurant.getLocation() != null) {
            this.location = new Location(restaurant.getLocation().getY(), restaurant.getLocation().getX());
        }
        this.rating = restaurant.getRating();
        this.sections = restaurant.getSections();
    }
}
