package com.tablehub.thbackend.dto.request;

import com.tablehub.thbackend.model.TableStatus;
import lombok.Data;

@Data
public class TableUpdateRequest {
    private Long restaurantId;
    private Long sectionId;
    private Long tableId;
    private TableStatus requestedStatus;
}