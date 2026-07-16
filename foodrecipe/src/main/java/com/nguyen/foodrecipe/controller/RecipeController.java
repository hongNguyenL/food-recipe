package com.nguyen.foodrecipe.controller;

import com.nguyen.foodrecipe.dto.*;
import com.nguyen.foodrecipe.security.UserPrincipal;
import java.util.List;
import com.nguyen.foodrecipe.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Tag(name = "Recipe", description = "Recipe browsing and interaction APIs")
public class RecipeController {

    private final RecipeService recipeService;
    private final FavoriteService favoriteService;
    private final RatingService ratingService;
    private final CommentService commentService;

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
            description = "Retrieve complete recipe details including category, ingredients, instructions, and statistics")
    public ResponseEntity<ApiResponse<RecipeDetailResponse>> getRecipeById(@PathVariable Long id) {
        RecipeDetailResponse response = recipeService.getRecipeById(id);
        return ResponseEntity.ok(ApiResponse.success("Recipe fetched successfully", response));
    }

    @GetMapping("/search")
    @Operation(summary = "Advanced search",
            description = "Search recipes by keyword, category, ingredient, or any combination. Supports pagination and sorting by title, createdAt, averageRating, favoriteCount, commentCount.")
    public ResponseEntity<ApiResponse<Page<SearchRecipeResponse>>> searchRecipes(
            @RequestParam(required = false) @Parameter(description = "Search keyword (matches title)") String keyword,
            @RequestParam(required = false) @Parameter(description = "Filter by category ID") Long categoryId,
            @RequestParam(required = false) @Parameter(description = "Filter by ingredient name (partial match)") String ingredient,
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        Page<SearchRecipeResponse> page = recipeService.advancedSearch(keyword, categoryId, ingredient, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results fetched successfully", page));
    }

    @GetMapping("/popular")
    @Operation(summary = "Popular recipes",
            description = "Return recipes sorted by a weighted popularity score based on average rating (weight 3), favorite count (weight 2), and comment count (weight 1).")
    public ResponseEntity<ApiResponse<Page<PopularRecipeResponse>>> getPopularRecipes(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PopularRecipeResponse> page = recipeService.getPopularRecipes(pageable);
        return ResponseEntity.ok(ApiResponse.success("Popular recipes fetched successfully", page));
    }

    @GetMapping("/top-rated")
    @Operation(summary = "Top rated recipes",
            description = "Return recipes ordered by average rating descending.")
    public ResponseEntity<ApiResponse<Page<SearchRecipeResponse>>> getTopRatedRecipes(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<SearchRecipeResponse> page = recipeService.getTopRatedRecipes(pageable);
        return ResponseEntity.ok(ApiResponse.success("Top rated recipes fetched successfully", page));
    }

    @GetMapping("/latest")
    @Operation(summary = "Latest recipes",
            description = "Return newest recipes ordered by creation date descending.")
    public ResponseEntity<ApiResponse<Page<RecipeSummaryResponse>>> getLatestRecipes(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<RecipeSummaryResponse> page = recipeService.getLatestRecipes(pageable);
        return ResponseEntity.ok(ApiResponse.success("Latest recipes fetched successfully", page));
    }

    @GetMapping("/{id}/similar")
    @Operation(summary = "Similar recipes",
            description = "Recommend up to 10 similar recipes based on shared category and common ingredients, ranked by relevance.")
    public ResponseEntity<ApiResponse<List<SimilarRecipeResponse>>> getSimilarRecipes(
            @PathVariable @Parameter(description = "Recipe ID") Long id) {
        List<SimilarRecipeResponse> similar = recipeService.getSimilarRecipes(id);
        return ResponseEntity.ok(ApiResponse.success("Similar recipes fetched successfully", similar));
    }

    @PostMapping("/{id}/favorite")
    @Operation(summary = "Add recipe to favorites", description = "Favorite a recipe. Returns 409 if already favorited.")
    public ResponseEntity<ApiResponse<Void>> addFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        favoriteService.addFavorite(userPrincipal.getId(), id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Recipe added to favorites"));
    }

    @DeleteMapping("/{id}/favorite")
    @Operation(summary = "Remove recipe from favorites", description = "Unfavorite a recipe.")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        favoriteService.removeFavorite(userPrincipal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Recipe removed from favorites"));
    }

    @PostMapping("/{id}/rating")
    @Operation(summary = "Rate a recipe", description = "Rate a recipe (1-5). Updates the existing rating if already rated.")
    public ResponseEntity<ApiResponse<RatingResponse>> rateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody RatingRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        RatingResponse response = ratingService.rateRecipe(userPrincipal.getId(), id, request.rating());
        return ResponseEntity.ok(ApiResponse.success("Recipe rated successfully", response));
    }

    @GetMapping("/{id}/rating")
    @Operation(summary = "Get recipe rating", description = "Returns average rating, total ratings, and current user's rating (if authenticated).")
    public ResponseEntity<ApiResponse<RecipeRatingResponse>> getRecipeRating(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = (userPrincipal != null) ? userPrincipal.getId() : null;
        RecipeRatingResponse response = ratingService.getRecipeRating(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Recipe rating fetched successfully", response));
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Add comment to recipe", description = "Create a comment on a recipe.")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CommentResponse response = commentService.createComment(userPrincipal.getId(), id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comment added successfully", response));
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "Get recipe comments", description = "Retrieve paginated comments for a recipe, ordered by newest first.")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getRecipeComments(
            @PathVariable Long id,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CommentResponse> page = commentService.getRecipeComments(id, pageable);
        return ResponseEntity.ok(ApiResponse.success("Comments fetched successfully", page));
    }
}
