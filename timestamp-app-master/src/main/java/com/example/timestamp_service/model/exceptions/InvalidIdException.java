package com.example.timestamp_service.model.exceptions;

import java.util.function.Supplier;

public class InvalidIdException extends Exception {
    public InvalidIdException(Long id) {
        super(String.format("Id %d not found", id));
    }
}
