package com.nguyen.foodrecipe.controller;

import com.nguyen.foodrecipe.dto.ApiResponse;
import com.nguyen.foodrecipe.dto.CategoryResponse;
import com.nguyen.foodrecipe.dto.RecipeSummaryResponse;
import com.nguyen.foodrecipe.service.CategoryService;
import com.nguyen.foodrecipe.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Category browsing APIs")
public class CategoryController {

    private final CategoryService categoryService;
    private final RecipeService recipeService;

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Categories fetched successfully", categories));
    }

    @GetMapping("/{id}/recipes")
    @Operation(summary = "Get recipes by category",
            description = "Retrieve paginated recipes within a specific category")
    public ResponseEntity<ApiResponse<Page<RecipeSummaryResponse>>> getRecipesByCategory(
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        Page<RecipeSummaryResponse> page = recipeService.getRecipesByCategory(id, pageable);
        return ResponseEntity.ok(ApiResponse.success("Recipes fetched successfully", page));
    }
}
