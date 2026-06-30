package com.nguyen.foodrecipe.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for a {@link com.nguyen.foodrecipe.entity.Recipe},
 * including its nested ingredients, instructions, and category.
 */
public record RecipeResponse(
        Long id,
        String title,
        String imageUrl,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        CategoryResponse category,
        List<IngredientResponse> ingredients,
        List<InstructionResponse> instructions
) {
}
