package com.nguyen.foodrecipe.service;

import com.nguyen.foodrecipe.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CollectionService {

    CollectionResponse createCollection(Long userId, CollectionRequest request);

    CollectionResponse updateCollection(Long userId, Long collectionId, CollectionRequest request);

    void deleteCollection(Long userId, Long collectionId);

    Page<CollectionSummaryResponse> getUserCollections(Long userId, Pageable pageable);

    Page<CollectionSummaryResponse> getPublicCollections(Pageable pageable);

    CollectionDetailResponse getCollectionById(Long userId, Long collectionId);

    CollectionDetailResponse getPublicCollectionById(Long collectionId);

    CollectionRecipeResponse addRecipeToCollection(Long userId, Long collectionId, Long recipeId);

    void removeRecipeFromCollection(Long userId, Long collectionId, Long recipeId);

    Page<CollectionRecipeResponse> getCollectionRecipes(Long userId, Long collectionId, Pageable pageable);

    Page<CollectionRecipeResponse> getPublicCollectionRecipes(Long collectionId, Pageable pageable);

    Page<CollectionSummaryResponse> searchCollections(String keyword, String ownerUsername, String visibility, Pageable pageable);
}
