package com.nguyen.foodrecipe.dto;

import java.util.List;

public record PantrySearchResult(
        Long recipeId,
        String title,
        String imageUrl,
        String categoryName,
        double averageRating,
        int matchPercentage,
        List<String> matchedIngredients,
        List<String> missingIngredients,
        int matchedCount,
        int missingCount,
        int totalIngredients,
        int appliedMinimumMatchPercentage
) {}
