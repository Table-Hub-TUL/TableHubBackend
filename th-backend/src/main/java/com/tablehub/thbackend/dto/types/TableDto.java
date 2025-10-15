package com.tablehub.thbackend.dto.types;

import com.tablehub.thbackend.model.RestaurantTable;
import com.tablehub.thbackend.model.TableStatus;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class TableDto {
    public long id;
    public TableStatus status;
    public PositionDto position;
    public int capacity;

    public TableDto(RestaurantTable restaurantTable) {
        this.id = restaurantTable.getId();
        this.status = restaurantTable.getStatus();
        this.capacity = restaurantTable.getCapacity();
    }
}
