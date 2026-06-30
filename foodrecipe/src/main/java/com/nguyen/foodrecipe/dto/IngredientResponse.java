package com.nguyen.foodrecipe.dto;

/**
 * Response DTO for a {@link com.nguyen.foodrecipe.entity.Ingredient}.
 */
public record IngredientResponse(
        Long id,
        String ingredientText
) {
}
