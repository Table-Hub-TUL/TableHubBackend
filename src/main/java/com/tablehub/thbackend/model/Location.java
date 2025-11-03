package com.tablehub.thbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Location {
    private double lat;
    private double lng;
}
