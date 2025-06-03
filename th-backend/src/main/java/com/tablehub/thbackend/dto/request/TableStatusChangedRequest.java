package com.tablehub.thbackend.dto.request;

import com.tablehub.thbackend.model.TableStatus;

public class TableStatusChangedRequest {
    private Long restaurantId;
    private Long sectionId;
    private Long tableId;
    private TableStatus newStatus;
    private Long changeTimestamp;

    public TableStatusChangedRequest() {
    }

    public TableStatusChangedRequest(Long restaurantId, Long sectionId, Long tableId, TableStatus newStatus, Long changeTimestamp) {
        this.restaurantId = restaurantId;
        this.sectionId = sectionId;
        this.tableId = tableId;
        this.newStatus = newStatus;
        this.changeTimestamp = changeTimestamp;
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

    public TableStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(TableStatus newStatus) {
        this.newStatus = newStatus;
    }

    public Long getChangeTimestamp() {
        return changeTimestamp;
    }

    public void setChangeTimestamp(Long changeTimestamp) {
        this.changeTimestamp = changeTimestamp;
    }
}
