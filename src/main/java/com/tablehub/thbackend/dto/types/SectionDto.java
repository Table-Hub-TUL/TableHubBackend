package com.tablehub.thbackend.dto.types;

import com.tablehub.thbackend.model.RestaurantSection;
import com.tablehub.thbackend.model.SectionName;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionDto {
    private Long id;
    private SectionName name;
    private List<TableDto> tables;
    private List<PointOfInterestDto> pois;
    private SectionLayoutDto layout;

    public SectionDto(RestaurantSection section) {
        this.id = section.getId();
        this.name = section.getName();

        this.tables = (section.getTables() != null)
                ? section.getTables().stream()
                .map(TableDto::new)
                .toList()
                : List.of();

        this.pois = (section.getPois() != null)
                ? section.getPois().stream()
                .map(PointOfInterestDto::new)
                .toList()
                : List.of();

        this.layout = (section.getLayout() != null)
                ? new SectionLayoutDto(section.getLayout())
                : null;
    }
}
