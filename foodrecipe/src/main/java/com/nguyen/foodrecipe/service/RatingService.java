package com.nguyen.foodrecipe.service;

import com.nguyen.foodrecipe.dto.RatingResponse;
import com.nguyen.foodrecipe.dto.RecipeRatingResponse;

public interface RatingService {

    RatingResponse rateRecipe(Long userId, Long recipeId, int rating);

    RecipeRatingResponse getRecipeRating(Long recipeId, Long currentUserId);
}
