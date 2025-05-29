package com.tablehub.thbackend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RestaurantResponseDto {
    private Long id;
    private String name;
    private String address;
    private Location location;
    private List<String> cuisine;
    private Double rating;
}
