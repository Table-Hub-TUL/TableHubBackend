package com.tablehub.thbackend.dto.request;


import com.tablehub.thbackend.dto.SectionDto;
import com.tablehub.thbackend.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Optional
// TODO: Delete later if not needed
public class RestaurantDetailedRequest {
    private Long id;
    private String name;
    private String address;
    private Location location;
    private List<String> cuisine;
    private Double rating;
    private List<SectionDto> sections;
}
