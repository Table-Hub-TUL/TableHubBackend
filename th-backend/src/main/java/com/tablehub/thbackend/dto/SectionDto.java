package com.tablehub.thbackend.dto;

import com.tablehub.thbackend.model.RestaurantSection;
import com.tablehub.thbackend.model.SectionName;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class SectionDto {
    private Long id;
    private SectionName name;
    public List<TableDto> tables;
    public List<PointOfInterestDto> pois;
    public SectionLayoutDto layout;

    public SectionDto(RestaurantSection section) {
        this.id = section.getId();
        this.name = section.getName();

        this.tables = (section.getTables() != null)
                ? section.getTables().stream()
                .map(TableDto::new)
                .toList()
                : List.of();

//        this.pois = (section.getPois() != null)
//                ? section.getPois().stream()
//                .map(PointOfInterestDto::new)
//                .toList()
//                : List.of();
//
//        this.layout = section.getLayout() != null
//                ? new SectionLayoutDto(section.getLayout())
//                : null;
    }
}
