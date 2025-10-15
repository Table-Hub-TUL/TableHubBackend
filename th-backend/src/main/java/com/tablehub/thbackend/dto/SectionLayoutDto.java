package com.tablehub.thbackend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SectionLayoutDto {
    public int viewportWidth;
    public int viewportHeight;
    public String shape; // SVG path string, ref: https://www.w3schools.com/graphics/svg_path.asp
}
