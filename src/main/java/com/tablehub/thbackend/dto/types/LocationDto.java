package com.tablehub.thbackend.dto.types;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    private double lat;
    private double lng;
}
