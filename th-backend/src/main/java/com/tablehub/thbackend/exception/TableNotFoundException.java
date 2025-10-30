package com.tablehub.thbackend.exception;

public class TableNotFoundException extends RuntimeException {
    public TableNotFoundException(Long id) {
        super("Table with ID " + id + " not found.");
    }
}
