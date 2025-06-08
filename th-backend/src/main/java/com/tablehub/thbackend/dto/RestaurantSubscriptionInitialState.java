package com.tablehub.thbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RestaurantSubscriptionInitialState {
    private Long id;
    private List<SectionDto> sections;
}
