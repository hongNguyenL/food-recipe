package com.nguyen.foodrecipe.exception;

public class DuplicateCollectionRecipeException extends RuntimeException {
    public DuplicateCollectionRecipeException() {
        super("Recipe already exists in this collection");
    }
}
