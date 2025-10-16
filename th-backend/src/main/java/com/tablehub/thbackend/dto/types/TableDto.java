package com.tablehub.thbackend.dto.types;

import com.tablehub.thbackend.model.RestaurantTable;
import com.tablehub.thbackend.model.TableStatus;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class TableDto {
    private long id;
    private TableStatus status;
    private PositionDto position;
    private int capacity;

    public TableDto(RestaurantTable restaurantTable) {
        this.id = restaurantTable.getId();
        this.status = restaurantTable.getStatus();
        this.capacity = restaurantTable.getCapacity();
    }
}
