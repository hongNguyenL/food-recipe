package com.nguyen.foodrecipe.controller;

import com.nguyen.foodrecipe.dto.*;
import com.nguyen.foodrecipe.security.UserPrincipal;
import com.nguyen.foodrecipe.service.CollectionService;
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
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Collection", description = "Recipe collection management APIs")
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping("/collections")
    @Operation(summary = "Create collection",
            description = "Create a new recipe collection. Only authenticated users may create collections.")
    public ResponseEntity<ApiResponse<CollectionResponse>> createCollection(
            @Valid @RequestBody CollectionRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CollectionResponse response = collectionService.createCollection(userPrincipal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Collection created successfully", response));
    }

    @PutMapping("/collections/{id}")
    @Operation(summary = "Update collection",
            description = "Update a collection. Only the owner may update.")
    public ResponseEntity<ApiResponse<CollectionResponse>> updateCollection(
            @PathVariable Long id,
            @Valid @RequestBody CollectionRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CollectionResponse response = collectionService.updateCollection(userPrincipal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Collection updated successfully", response));
    }

    @DeleteMapping("/collections/{id}")
    @Operation(summary = "Delete collection",
            description = "Delete a collection. Only the owner or an ADMIN may delete.")
    public ResponseEntity<ApiResponse<Void>> deleteCollection(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        collectionService.deleteCollection(userPrincipal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Collection deleted successfully"));
    }

    @GetMapping("/users/me/collections")
    @Operation(summary = "Get my collections",
            description = "Return all collections owned by the authenticated user. Supports pagination.")
    public ResponseEntity<ApiResponse<Page<CollectionSummaryResponse>>> getMyCollections(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CollectionSummaryResponse> page = collectionService.getUserCollections(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Collections fetched successfully", page));
    }

    @GetMapping("/collections/public")
    @Operation(summary = "Get public collections",
            description = "Return only public collections. Supports pagination and sorting.")
    public ResponseEntity<ApiResponse<Page<CollectionSummaryResponse>>> getPublicCollections(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CollectionSummaryResponse> page = collectionService.getPublicCollections(pageable);
        return ResponseEntity.ok(ApiResponse.success("Public collections fetched successfully", page));
    }

    @GetMapping("/collections/search")
    @Operation(summary = "Search collections",
            description = "Search collections by keyword, owner username, and/or visibility. Supports pagination and sorting. Case-insensitive.")
    public ResponseEntity<ApiResponse<Page<CollectionSummaryResponse>>> searchCollections(
            @RequestParam(required = false) @Parameter(description = "Search keyword (matches collection name)") String keyword,
            @RequestParam(required = false) @Parameter(description = "Filter by owner username") String ownerUsername,
            @RequestParam(required = false) @Parameter(description = "Filter by visibility (PUBLIC or PRIVATE)") String visibility,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CollectionSummaryResponse> page = collectionService.searchCollections(keyword, ownerUsername, visibility, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results fetched successfully", page));
    }

    @GetMapping("/collections/{id}")
    @Operation(summary = "Get collection details",
            description = "Get collection details. If PUBLIC, anyone may view. If PRIVATE, only the owner or ADMIN may view.")
    public ResponseEntity<ApiResponse<CollectionDetailResponse>> getCollection(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CollectionDetailResponse response;
        if (userPrincipal != null) {
            response = collectionService.getCollectionById(userPrincipal.getId(), id);
        } else {
            response = collectionService.getPublicCollectionById(id);
        }
        return ResponseEntity.ok(ApiResponse.success("Collection fetched successfully", response));
    }

    @PostMapping("/collections/{collectionId}/recipes/{recipeId}")
    @Operation(summary = "Add recipe to collection",
            description = "Add a recipe to a collection. Only the owner may add. Returns 409 if the recipe already exists in the collection.")
    public ResponseEntity<ApiResponse<CollectionRecipeResponse>> addRecipeToCollection(
            @PathVariable Long collectionId,
            @PathVariable Long recipeId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CollectionRecipeResponse response = collectionService.addRecipeToCollection(userPrincipal.getId(), collectionId, recipeId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Recipe added to collection successfully", response));
    }

    @DeleteMapping("/collections/{collectionId}/recipes/{recipeId}")
    @Operation(summary = "Remove recipe from collection",
            description = "Remove a recipe from a collection. Only the owner may remove.")
    public ResponseEntity<ApiResponse<Void>> removeRecipeFromCollection(
            @PathVariable Long collectionId,
            @PathVariable Long recipeId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        collectionService.removeRecipeFromCollection(userPrincipal.getId(), collectionId, recipeId);
        return ResponseEntity.ok(ApiResponse.success("Recipe removed from collection successfully"));
    }

    @GetMapping("/collections/{collectionId}/recipes")
    @Operation(summary = "List recipes in collection",
            description = "List all recipes in a collection. Supports pagination.")
    public ResponseEntity<ApiResponse<Page<CollectionRecipeResponse>>> getCollectionRecipes(
            @PathVariable Long collectionId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CollectionRecipeResponse> page;
        if (userPrincipal != null) {
            page = collectionService.getCollectionRecipes(userPrincipal.getId(), collectionId, pageable);
        } else {
            page = collectionService.getPublicCollectionRecipes(collectionId, pageable);
        }
        return ResponseEntity.ok(ApiResponse.success("Recipes fetched successfully", page));
    }
}
