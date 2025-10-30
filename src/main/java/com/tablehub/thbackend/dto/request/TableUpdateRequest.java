package com.tablehub.thbackend.dto.request;

import com.tablehub.thbackend.model.TableStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TableUpdateRequest {
    @NotNull
    private Long restaurantId;
    @NotNull
    private Long sectionId;
    @NotNull
    private Long tableId;
    @NotNull
    private TableStatus requestedStatus;
}