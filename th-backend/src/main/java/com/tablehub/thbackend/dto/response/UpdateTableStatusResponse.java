package com.tablehub.thbackend.dto.response;

import com.tablehub.thbackend.model.TableStatus;

public class UpdateTableStatusResponse {
    private Long restaurantId;
    private Long sectionId;
    private Long tableId;
    private boolean updateSuccess;
    private TableStatus resultingStatus;
    private String message;
    private Integer pointsAwarded;

    public UpdateTableStatusResponse() {
    }

    public UpdateTableStatusResponse(Long restaurantId, Long sectionId, Long tableId, boolean updateSuccess, TableStatus resultingStatus, String message, Integer pointsAwarded) {
        this.restaurantId = restaurantId;
        this.sectionId = sectionId;
        this.tableId = tableId;
        this.updateSuccess = updateSuccess;
        this.resultingStatus = resultingStatus;
        this.message = message;
        this.pointsAwarded = pointsAwarded;
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

    public boolean isUpdateSuccess() {
        return updateSuccess;
    }

    public void setUpdateSuccess(boolean updateSuccess) {
        this.updateSuccess = updateSuccess;
    }

    public TableStatus getResultingStatus() {
        return resultingStatus;
    }

    public void setResultingStatus(TableStatus resultingStatus) {
        this.resultingStatus = resultingStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(Integer pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }
}
