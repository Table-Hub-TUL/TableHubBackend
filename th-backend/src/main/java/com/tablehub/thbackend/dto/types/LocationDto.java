package com.tablehub.thbackend.dto.types;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationDto {
    private double lat;
    private double lng;
}
