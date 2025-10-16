package com.tablehub.thbackend.dto.types;


import com.tablehub.thbackend.model.Position;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionDto {
    private double x;
    private double y;
    public PositionDto(Position point) {
        this.x = point.getX();
        this.y = point.getY();
    }
}
