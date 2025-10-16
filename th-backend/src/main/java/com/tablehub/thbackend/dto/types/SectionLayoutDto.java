package com.tablehub.thbackend.dto.types;

import com.tablehub.thbackend.model.SectionLayout;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SectionLayoutDto {
    private int viewportWidth;
    private int viewportHeight;
    private String shape; // SVG path string, ref: https://www.w3schools.com/graphics/svg_path.asp

    public SectionLayoutDto(SectionLayout layout) {
        this.viewportWidth = layout.getViewportWidth();
        this.viewportHeight = layout.getViewportHeight();
        this.shape = layout.getShape();
    }
}
