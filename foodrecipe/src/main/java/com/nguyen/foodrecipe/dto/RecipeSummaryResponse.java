package com.nguyen.foodrecipe.dto;

public record RecipeSummaryResponse(
    Long id,
    String title,
    String imageUrl,
    String categoryName
) {}
