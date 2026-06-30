package com.nguyen.foodrecipe.controller;

import com.nguyen.foodrecipe.dto.ApiResponse;
import com.nguyen.foodrecipe.dto.RecipeRequest;
import com.nguyen.foodrecipe.dto.RecipeResponse;
import com.nguyen.foodrecipe.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing recipes.
 * <p>
 * Supports CRUD, pagination, sorting, keyword search, and category filtering.
 * All business logic is delegated to {@link RecipeService}.
 * </p>
 */
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Tag(name = "Recipe", description = "Recipe management APIs")
public class RecipeController {

    private final RecipeService recipeService;

    /** Creates a new recipe. */
    @PostMapping
    @Operation(summary = "Create a new recipe")
    public ResponseEntity<ApiResponse<RecipeResponse>> createRecipe(
            @Valid @RequestBody RecipeRequest request) {
        RecipeResponse response = recipeService.createRecipe(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Recipe created successfully", response));
    }

    /** Updates an existing recipe (full replacement). */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing recipe")
    public ResponseEntity<ApiResponse<RecipeResponse>> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody RecipeRequest request) {
        RecipeResponse response = recipeService.updateRecipe(id, request);
        return ResponseEntity.ok(ApiResponse.success("Recipe updated successfully", response));
    }

    /** Deletes a recipe by ID. */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a recipe")
    public ResponseEntity<ApiResponse<Void>> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.ok(ApiResponse.success("Recipe deleted successfully"));
    }

    /** Returns a single recipe with full details. */
    @GetMapping("/{id}")
    @Operation(summary = "Get a recipe by ID")
    public ResponseEntity<ApiResponse<RecipeResponse>> getRecipeById(@PathVariable Long id) {
        RecipeResponse response = recipeService.getRecipeById(id);
        return ResponseEntity.ok(ApiResponse.success("Recipe fetched successfully", response));
    }

    /** Returns a paginated, sortable list of all recipes. */
    @GetMapping
    @Operation(summary = "Get all recipes (paginated)")
    public ResponseEntity<ApiResponse<Page<RecipeResponse>>> getAllRecipes(
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        Page<RecipeResponse> page = recipeService.getAllRecipes(pageable);
        return ResponseEntity.ok(ApiResponse.success("Recipes fetched successfully", page));
    }

    /** Searches recipes by title keyword (PostgreSQL ILIKE). */
    @GetMapping("/search")
    @Operation(summary = "Search recipes by title")
    public ResponseEntity<ApiResponse<Page<RecipeResponse>>> searchRecipes(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        Page<RecipeResponse> page = recipeService.searchRecipes(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results fetched successfully", page));
    }

    /** Filters recipes by category ID. */
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get recipes by category")
    public ResponseEntity<ApiResponse<Page<RecipeResponse>>> getRecipesByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        Page<RecipeResponse> page = recipeService.getRecipesByCategory(categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Recipes fetched successfully", page));
    }
}
