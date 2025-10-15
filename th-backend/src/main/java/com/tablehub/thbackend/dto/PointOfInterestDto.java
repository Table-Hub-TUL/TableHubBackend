package com.tablehub.thbackend.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PointOfInterestDto {
    public int id;
    public String description; // free text
    public PointDto topLeft;
    public PointDto bottomRight;
    public List<Integer> otherSectionPoiLink;
}
