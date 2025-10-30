package com.tablehub.thbackend.dto;

import com.tablehub.thbackend.dto.types.SectionDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RestaurantSubscriptionInitialState {
    private Long id;
    private List<SectionDto> sections;
}
