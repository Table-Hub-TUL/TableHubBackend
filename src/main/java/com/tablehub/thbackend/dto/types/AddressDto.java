package com.tablehub.thbackend.dto.types;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    private int streetNumber;
    private String streetName;
    private Integer apartmentNumber; // nullable
    private String city;
    private String postalCode;
    private String country;
}
