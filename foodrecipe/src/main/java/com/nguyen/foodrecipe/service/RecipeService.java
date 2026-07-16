package com.nguyen.foodrecipe.service;

import com.nguyen.foodrecipe.dto.RecipeDetailResponse;
import com.nguyen.foodrecipe.dto.RecipeRequest;
import com.nguyen.foodrecipe.dto.RecipeResponse;
import com.nguyen.foodrecipe.dto.RecipeSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipeService {

    RecipeResponse createRecipe(RecipeRequest request);

    RecipeResponse updateRecipe(Long id, RecipeRequest request);

    void deleteRecipe(Long id);

    RecipeDetailResponse getRecipeById(Long id);

    Page<RecipeSummaryResponse> getAllRecipes(String keyword, Pageable pageable);

    Page<RecipeSummaryResponse> searchRecipes(String keyword, Pageable pageable);

    Page<RecipeSummaryResponse> getRecipesByCategory(Long categoryId, Pageable pageable);
}
