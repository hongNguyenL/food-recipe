package com.nguyen.foodrecipe.exception;

public class CategoryHasRecipesException extends RuntimeException {
    public CategoryHasRecipesException(Long categoryId) {
        super("Cannot delete category with id: " + categoryId + " because it still contains recipes");
    }
}
