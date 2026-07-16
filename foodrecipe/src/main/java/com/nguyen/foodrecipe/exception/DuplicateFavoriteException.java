package com.nguyen.foodrecipe.exception;

public class DuplicateFavoriteException extends RuntimeException {
    public DuplicateFavoriteException(Long recipeId) {
        super("Recipe already favorited: " + recipeId);
    }
}
