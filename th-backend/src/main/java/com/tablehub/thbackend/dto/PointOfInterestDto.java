package com.tablehub.thbackend.dto;

import java.util.List;

public class PointOfInterestDto {
    public int id;
    public String description; // free text
    public PointDto topLeft;
    public PointDto bottomRight;
    public List<Integer> otherSectionPoiLink;
}
