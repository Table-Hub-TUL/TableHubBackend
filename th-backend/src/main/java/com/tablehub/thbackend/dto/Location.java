package com.tablehub.thbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Location {
    private double lat;
    private double lng;
}
