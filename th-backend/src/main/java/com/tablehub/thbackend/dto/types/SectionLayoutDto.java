package com.tablehub.thbackend.dto.types;

import com.tablehub.thbackend.model.SectionLayout;
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

    public SectionLayoutDto(SectionLayout layout) {
        this.viewportWidth = layout.getViewportWidth();
        this.viewportHeight = layout.getViewportHeight();
        this.shape = layout.getShape();
    }
}
