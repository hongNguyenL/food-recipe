package com.nguyen.foodrecipe.dto;

public record RecipeStatisticsResponse(
        long favoriteCount,
        double averageRating,
        long totalRatings,
        long totalComments
) {}
