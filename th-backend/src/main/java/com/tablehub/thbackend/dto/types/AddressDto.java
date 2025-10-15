package com.tablehub.thbackend.dto.types;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddressDto {
    public int streetNumber;
    public String streetName;
    public Integer apartmentNumber; // nullable
    public String city;
    public String postalCode;
    public String country;
}
