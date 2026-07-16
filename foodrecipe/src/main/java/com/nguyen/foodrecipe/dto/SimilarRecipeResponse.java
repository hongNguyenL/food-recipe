package com.nguyen.foodrecipe.dto;

public record SimilarRecipeResponse(
    Long id,
    String title,
    String imageUrl,
    String categoryName,
    double averageRating
) {}
