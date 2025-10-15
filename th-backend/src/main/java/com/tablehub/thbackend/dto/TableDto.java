package com.tablehub.thbackend.dto;

import com.tablehub.thbackend.model.TableStatus;

public class TableDto {
    public int id;
    public TableStatus status;
    public PointDto position;
    public int capacity;
}
