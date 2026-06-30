package com.nguyen.foodrecipe.exception;

/**
 * Thrown when a requested recipe cannot be found.
 */
public class RecipeNotFoundException extends RuntimeException {

    public RecipeNotFoundException(Long id) {
        super("Recipe not found with id: " + id);
    }

    public RecipeNotFoundException(String message) {
        super(message);
    }
}
