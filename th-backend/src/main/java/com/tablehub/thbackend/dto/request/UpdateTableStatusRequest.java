package com.tablehub.thbackend.dto.request;

import com.tablehub.thbackend.model.TableStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UpdateTableStatusRequest {
    private Long restaurantId;
    private Long sectionId;
    private Long tableId;
    private TableStatus requestedStatus;

}
