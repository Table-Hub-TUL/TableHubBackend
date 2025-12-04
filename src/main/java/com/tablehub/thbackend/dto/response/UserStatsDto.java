package com.tablehub.thbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatsDto {
    private int points;
    private int reportsCount;
    private int ranking;
}