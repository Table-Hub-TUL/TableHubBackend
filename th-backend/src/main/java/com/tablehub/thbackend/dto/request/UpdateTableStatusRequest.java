package com.tablehub.thbackend.dto.request;

import com.tablehub.thbackend.model.TableStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UpdateTableStatusRequest {
    @NotNull
    private Long restaurantId;
    @NotNull
    private Long sectionId;
    @NotNull
    private Long tableId;
    @NotNull
    private TableStatus requestedStatus;

}
