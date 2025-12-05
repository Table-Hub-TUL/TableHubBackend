package com.tablehub.thbackend.dto.response;

import com.tablehub.thbackend.model.Action;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AchievementDto {
    private Long id;
    private String title;
    private String emoji;
    private Long points;

    public AchievementDto(Action action) {
        this.id = action.getId();
        this.title = action.getName();
        this.points = (long) action.getPoints();
        this.emoji = null; // Default or map based on naming convention
    }
}