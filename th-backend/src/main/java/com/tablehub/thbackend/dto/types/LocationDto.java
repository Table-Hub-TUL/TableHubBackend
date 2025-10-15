package com.tablehub.thbackend.dto.types;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationDto {
        public double lat;
        public double lng;
}
