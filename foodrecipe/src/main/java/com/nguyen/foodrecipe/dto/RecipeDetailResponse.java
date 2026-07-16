package com.nguyen.foodrecipe.dto;

import java.util.List;

public record RecipeDetailResponse(
    Long id,
    String title,
    String imageUrl,
    String description,
    CategoryResponse category,
    List<IngredientResponse> ingredients,
    List<InstructionResponse> instructions
) {}
