package com.tablehub.thbackend.dto.response;


import com.tablehub.thbackend.dto.types.SectionDto;
import com.tablehub.thbackend.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class RestaurantDetailedResponse {
    private Long id;
    private List<SectionDto> sections;

    public RestaurantDetailedResponse(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.sections = restaurant.getSections().stream()
                .map(SectionDto::new)
                .toList();
    }
}
