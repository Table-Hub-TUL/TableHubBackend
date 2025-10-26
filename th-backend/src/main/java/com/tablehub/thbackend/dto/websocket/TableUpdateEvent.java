package com.tablehub.thbackend.dto.websocket;

import com.tablehub.thbackend.model.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableUpdateEvent {
    private long restaurantID;
    private long tableID;
    private String region;
    private TableStatus tableStatus;
}
