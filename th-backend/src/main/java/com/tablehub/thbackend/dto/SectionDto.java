package com.tablehub.thbackend.dto;

import com.tablehub.thbackend.model.RestaurantSection;
import com.tablehub.thbackend.model.SectionName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionDto {
    private Long id;
    private SectionName name;

    public SectionDto(RestaurantSection section) {
        this.id = section.getId();
        this.name = section.getName();
    }
}
