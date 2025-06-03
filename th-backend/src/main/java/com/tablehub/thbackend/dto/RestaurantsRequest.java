package com.tablehub.thbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RestaurantsRequest {
    private Location localization;
    private double radius;
    private List<String> filters;
}
