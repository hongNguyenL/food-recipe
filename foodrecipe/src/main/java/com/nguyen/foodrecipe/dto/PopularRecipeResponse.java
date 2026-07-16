package com.nguyen.foodrecipe.dto;

public record PopularRecipeResponse(
    Long id,
    String title,
    String imageUrl,
    String categoryName,
    double averageRating,
    long favoriteCount,
    long commentCount,
    double popularityScore
) {}
