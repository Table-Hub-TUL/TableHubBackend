package com.tablehub.thbackend.dto.response;


import com.tablehub.thbackend.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantResponseDto {
    private Long id;
    private String name;
    private String address;
    private Location location;
    private List<String> cuisine;
    private Double rating;

}
