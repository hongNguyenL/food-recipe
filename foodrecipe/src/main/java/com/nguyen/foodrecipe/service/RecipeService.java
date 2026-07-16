package com.nguyen.foodrecipe.service;

import com.nguyen.foodrecipe.dto.PopularRecipeResponse;
import com.nguyen.foodrecipe.dto.RecipeDetailResponse;
import com.nguyen.foodrecipe.dto.RecipeRequest;
import com.nguyen.foodrecipe.dto.RecipeResponse;
import com.nguyen.foodrecipe.dto.RecipeSummaryResponse;
import com.nguyen.foodrecipe.dto.SearchRecipeResponse;
import com.nguyen.foodrecipe.dto.SimilarRecipeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeService {

    RecipeResponse createRecipe(RecipeRequest request);

    RecipeResponse updateRecipe(Long id, RecipeRequest request);

    void deleteRecipe(Long id);

    RecipeDetailResponse getRecipeById(Long id);

    Page<RecipeSummaryResponse> getAllRecipes(String keyword, Pageable pageable);

    Page<RecipeSummaryResponse> searchRecipes(String keyword, Pageable pageable);

    Page<RecipeSummaryResponse> getRecipesByCategory(Long categoryId, Pageable pageable);

    Page<SearchRecipeResponse> advancedSearch(String keyword, Long categoryId, String ingredient, Pageable pageable);

    Page<PopularRecipeResponse> getPopularRecipes(Pageable pageable);

    Page<SearchRecipeResponse> getTopRatedRecipes(Pageable pageable);

    Page<RecipeSummaryResponse> getLatestRecipes(Pageable pageable);

    List<SimilarRecipeResponse> getSimilarRecipes(Long recipeId);
}
