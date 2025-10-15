package com.tablehub.thbackend.dto.types;

import com.tablehub.thbackend.model.PointOfInterest;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointOfInterestDto {
    // public int id;
    private String description; // free text
    private PositionDto topLeft;
    private PositionDto bottomRight;
//    private List<Integer> otherSectionPoiLink;

    public PointOfInterestDto(PointOfInterest pointOfInterest) {
        this.description = pointOfInterest.getDescription();

        this.topLeft = (pointOfInterest.getTopLeft() != null)
                ? new PositionDto(pointOfInterest.getTopLeft())
                : null;

        this.bottomRight = (pointOfInterest.getBottomRight() != null)
                ? new PositionDto(pointOfInterest.getBottomRight())
                : null;
    }
}