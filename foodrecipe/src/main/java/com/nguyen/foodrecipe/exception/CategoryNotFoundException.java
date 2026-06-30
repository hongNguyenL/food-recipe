package com.nguyen.foodrecipe.exception;

/**
 * Thrown when a requested category cannot be found.
 */
public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long id) {
        super("Category not found with id: " + id);
    }

    public CategoryNotFoundException(String message) {
        super(message);
    }
}
