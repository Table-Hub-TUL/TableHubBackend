package com.tablehub.thbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsDto {
    private int points;
    private int reportsCount;
    private int ranking;
}