package com.nguyen.foodrecipe.exception;

public class CollectionNotFoundException extends RuntimeException {
    public CollectionNotFoundException(Long id) {
        super("Collection not found with id: " + id);
    }
}
