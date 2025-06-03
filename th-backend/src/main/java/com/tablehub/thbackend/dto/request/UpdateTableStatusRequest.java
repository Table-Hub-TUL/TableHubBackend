package com.tablehub.thbackend.dto.request;

import com.tablehub.thbackend.model.TableStatus;

public class UpdateTableStatusRequest {
    private Long restaurantId;
    private Long sectionId;
    private Long tableId;
    private TableStatus requestedStatus;

    public UpdateTableStatusRequest() {
    }

    public UpdateTableStatusRequest(Long restaurantId, Long sectionId, Long tableId, TableStatus requestedStatus) {
        this.restaurantId = restaurantId;
        this.sectionId = sectionId;
        this.tableId = tableId;
        this.requestedStatus = requestedStatus;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public TableStatus getRequestedStatus() {
        return requestedStatus;
    }

    public void setRequestedStatus(TableStatus requestedStatus) {
        this.requestedStatus = requestedStatus;
    }
}
