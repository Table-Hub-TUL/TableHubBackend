package com.tablehub.thbackend.dto.response;

import com.tablehub.thbackend.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantSimpleResponse {
    private Long id;
    private String name;
    private List<CuisineName> cuisine;
    private Address address;
    private Location location;
    private Double rating = 0.0;
    private Integer freeTableCount;
    private Integer totalTableCount;

    public RestaurantSimpleResponse(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.cuisine = List.of(restaurant.getCuisineName());
        this.address = restaurant.getAddress();
        if (restaurant.getLocation() != null) {
            this.location = new Location(restaurant.getLocation().getY(), restaurant.getLocation().getX());
        }
        this.rating = restaurant.getRating();

        if (restaurant.getSections() != null) {
            this.freeTableCount = (int) restaurant.getSections().stream()
                    .flatMap(section -> section.getTables() != null ? section.getTables().stream() : Stream.empty())
                    .filter(table -> TableStatus.AVAILABLE.equals(table.getStatus()))
                    .count();

            this.totalTableCount = restaurant.getSections().stream()
                    .mapToInt(section -> section.getTables() != null ? section.getTables().size() : 0)
                    .sum();
        } else {
            this.freeTableCount = 0;
            this.totalTableCount = 0;
        }
    }
}