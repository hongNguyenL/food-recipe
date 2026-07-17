package com.nguyen.foodrecipe.exception;

public class CollectionAccessDeniedException extends RuntimeException {
    public CollectionAccessDeniedException(String message) {
        super(message);
    }
}
