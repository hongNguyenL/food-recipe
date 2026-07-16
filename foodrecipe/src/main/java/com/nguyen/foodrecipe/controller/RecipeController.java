package com.nguyen.foodrecipe.controller;

import com.nguyen.foodrecipe.dto.ApiResponse;
import com.nguyen.foodrecipe.dto.RecipeDetailResponse;
import com.nguyen.foodrecipe.dto.RecipeSummaryResponse;
import com.nguyen.foodrecipe.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Tag(name = "Recipe", description = "Recipe browsing APIs")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    @Operation(summary = "Get all recipes",
            description = "Retrieve a paginated, sorted list of recipes, optionally filtered by keyword (case-insensitive title search)")
    public ResponseEntity<ApiResponse<Page<RecipeSummaryResponse>>> getAllRecipes(
            @RequestParam(required = false) @Parameter(description = "Optional keyword to filter recipes by title") String keyword,
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        Page<RecipeSummaryResponse> page = recipeService.getAllRecipes(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("Recipes fetched successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get recipe by ID",
            description = "Retrieve complete recipe details including category, ingredients, and instructions")
    public ResponseEntity<ApiResponse<RecipeDetailResponse>> getRecipeById(@PathVariable Long id) {
        RecipeDetailResponse response = recipeService.getRecipeById(id);
        return ResponseEntity.ok(ApiResponse.success("Recipe fetched successfully", response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search recipes by title",
            description = "Search recipes whose title contains the keyword (case-insensitive)")
    public ResponseEntity<ApiResponse<Page<RecipeSummaryResponse>>> searchRecipes(
            @RequestParam @Parameter(description = "Search keyword") String keyword,
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        Page<RecipeSummaryResponse> page = recipeService.searchRecipes(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results fetched successfully", page));
    }
}
