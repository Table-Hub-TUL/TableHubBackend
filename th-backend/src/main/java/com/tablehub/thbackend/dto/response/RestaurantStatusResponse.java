package com.tablehub.thbackend.dto.response;


import java.time.Instant;

public class RestaurantStatusResponse {
    private Long restaurantId;
    private String name;
    private int freeTableCount;
    private int totalTableCount;
    private Instant timestamp;

    public RestaurantStatusResponse() {}

    public RestaurantStatusResponse(Long restaurantId,
                                    String name,
                                    int freeTableCount,
                                    int totalTableCount,
                                    Instant timestamp) {
        this.restaurantId    = restaurantId;
        this.name            = name;
        this.freeTableCount  = freeTableCount;
        this.totalTableCount = totalTableCount;
        this.timestamp       = timestamp;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getFreeTableCount() {
        return freeTableCount;
    }
    public void setFreeTableCount(int freeTableCount) {
        this.freeTableCount = freeTableCount;
    }

    public int getTotalTableCount() {
        return totalTableCount;
    }
    public void setTotalTableCount(int totalTableCount) {
        this.totalTableCount = totalTableCount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
