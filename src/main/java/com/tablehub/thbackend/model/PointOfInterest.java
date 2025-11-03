package com.tablehub.thbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
// TODO: missing List<Integer> otherSectionPoiLink; id; check if needed
public class PointOfInterest {
    private double id;

    private String description;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "top_left_x")),
            @AttributeOverride(name = "y", column = @Column(name = "top_left_y"))
    })
    private Position topLeft;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "bottom_right_x")),
            @AttributeOverride(name = "y", column = @Column(name = "bottom_right_y"))
    })
    private Position bottomRight;
}
