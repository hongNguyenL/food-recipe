package com.nguyen.foodrecipe.dto;

import java.time.LocalDateTime;

public record SearchRecipeResponse(
    Long id,
    String title,
    String imageUrl,
    String categoryName,
    LocalDateTime createdAt,
    double averageRating,
    long favoriteCount,
    long commentCount
) {}
