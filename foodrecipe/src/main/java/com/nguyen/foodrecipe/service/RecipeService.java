package com.nguyen.foodrecipe.service;

import com.nguyen.foodrecipe.dto.RecipeRequest;
import com.nguyen.foodrecipe.dto.RecipeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for {@link com.nguyen.foodrecipe.entity.Recipe} operations.
 */
public interface RecipeService {

    /**
     * Creates a new recipe with its ingredients and instructions.
     *
     * @param request the recipe data
     * @return the created recipe
     */
    RecipeResponse createRecipe(RecipeRequest request);

    /**
     * Updates an existing recipe, replacing its ingredients and instructions.
     *
     * @param id      the recipe ID
     * @param request the updated recipe data
     * @return the updated recipe
     */
    RecipeResponse updateRecipe(Long id, RecipeRequest request);

    /**
     * Deletes a recipe by ID.
     *
     * @param id the recipe ID
     */
    void deleteRecipe(Long id);

    /**
     * Returns a single recipe by ID, including all nested details.
     *
     * @param id the recipe ID
     * @return the recipe with ingredients, instructions, and category
     */
    RecipeResponse getRecipeById(Long id);

    /**
     * Returns a paginated list of all recipes.
     *
     * @param pageable pagination and sorting parameters
     * @return a page of recipes
     */
    Page<RecipeResponse> getAllRecipes(Pageable pageable);

    /**
     * Searches recipes by title using case-insensitive matching.
     *
     * @param keyword  the search keyword
     * @param pageable pagination and sorting parameters
     * @return a page of matching recipes
     */
    Page<RecipeResponse> searchRecipes(String keyword, Pageable pageable);

    /**
     * Filters recipes by category.
     *
     * @param categoryId the category ID to filter by
     * @param pageable   pagination and sorting parameters
     * @return a page of recipes in the specified category
     */
    Page<RecipeResponse> getRecipesByCategory(Long categoryId, Pageable pageable);
}
