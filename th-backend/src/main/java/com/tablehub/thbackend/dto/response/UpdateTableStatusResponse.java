package com.tablehub.thbackend.dto.response;

import com.tablehub.thbackend.model.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTableStatusResponse {
    private Long restaurantId;
    private Long sectionId;
    private Long tableId;
    private boolean updateSuccess;
    private TableStatus resultingStatus;
    private String message;
    private Integer pointsAwarded;
}
