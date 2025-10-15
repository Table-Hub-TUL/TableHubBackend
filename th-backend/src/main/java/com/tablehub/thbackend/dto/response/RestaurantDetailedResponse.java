package com.tablehub.thbackend.dto.response;


import com.tablehub.thbackend.dto.SectionDto;
import com.tablehub.thbackend.model.*;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDetailedResponse {
    private Long id;
    private List<SectionDto> sections;

    public RestaurantDetailedResponse(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.sections = restaurant.getSections() != null
                ? restaurant.getSections().stream()
                .map(section -> new SectionDto())
                .toList()
                : List.of();
    }
}
