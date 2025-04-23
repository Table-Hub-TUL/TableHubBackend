package com.tablehub.thbackend.dto;

public class UpdateTableStatusRequest {
    private Long restaurantId;
    private Long sectionId;
    private Long tableId;
    private TableStatusEnum requestedStatus;

    public UpdateTableStatusRequest() {
    }

    public UpdateTableStatusRequest(Long restaurantId, Long sectionId, Long tableId, TableStatusEnum requestedStatus) {
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

    public TableStatusEnum getRequestedStatus() {
        return requestedStatus;
    }

    public void setRequestedStatus(TableStatusEnum requestedStatus) {
        this.requestedStatus = requestedStatus;
    }
}
