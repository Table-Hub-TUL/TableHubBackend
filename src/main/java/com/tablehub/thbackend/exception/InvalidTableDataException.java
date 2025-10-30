package com.tablehub.thbackend.exception;

public class InvalidTableDataException extends RuntimeException {
    public InvalidTableDataException(String message) {
        super(message);
    }
}