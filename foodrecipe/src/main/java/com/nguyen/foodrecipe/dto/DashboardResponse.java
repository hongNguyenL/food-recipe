package com.nguyen.foodrecipe.dto;

import java.util.List;

public record DashboardResponse(
    long totalRecipes,
    long totalUsers,
    long totalCategories,
    long totalFavorites,
    long totalRatings,
    long totalComments,
    double averageRating,
    List<AdminUserResponse> newestUsers,
    List<RecipeSummaryResponse> newestRecipes
) {}
