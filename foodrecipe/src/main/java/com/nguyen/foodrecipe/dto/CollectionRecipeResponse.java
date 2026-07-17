package com.nguyen.foodrecipe.dto;

import java.time.LocalDateTime;

public record CollectionRecipeResponse(
        Long id,
        Long recipeId,
        String recipeTitle,
        String recipeImageUrl,
        LocalDateTime addedAt
) {}
