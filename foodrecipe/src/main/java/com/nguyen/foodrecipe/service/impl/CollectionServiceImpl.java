package com.nguyen.foodrecipe.service.impl;

import com.nguyen.foodrecipe.dto.*;
import com.nguyen.foodrecipe.entity.*;
import com.nguyen.foodrecipe.exception.*;
import com.nguyen.foodrecipe.mapper.CollectionMapper;
import com.nguyen.foodrecipe.repository.CollectionRecipeRepository;
import com.nguyen.foodrecipe.repository.CollectionRepository;
import com.nguyen.foodrecipe.repository.RecipeRepository;
import com.nguyen.foodrecipe.repository.UserRepository;
import com.nguyen.foodrecipe.service.CollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final CollectionRecipeRepository collectionRecipeRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final CollectionMapper collectionMapper;

    @Override
    @Transactional
    public CollectionResponse createCollection(Long userId, CollectionRequest request) {
        log.debug("Creating collection for user id: {}", userId);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        RecipeCollection collection = collectionMapper.toEntity(request);
        collection.setOwner(owner);

        RecipeCollection saved = collectionRepository.save(collection);
        log.info("Collection created successfully with id: {}", saved.getId());

        return collectionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CollectionResponse updateCollection(Long userId, Long collectionId, CollectionRequest request) {
        log.debug("Updating collection id: {} by user id: {}", collectionId, userId);

        RecipeCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(collectionId));

        if (!collection.getOwner().getId().equals(userId)) {
            throw new UnauthorizedModificationException("You do not have permission to update this collection");
        }

        collection.setName(request.name());
        collection.setDescription(request.description());
        collection.setVisibility(CollectionVisibility.valueOf(request.visibility()));

        RecipeCollection updated = collectionRepository.save(collection);
        log.info("Collection updated successfully with id: {}", updated.getId());

        return collectionMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCollection(Long userId, Long collectionId) {
        log.debug("Deleting collection id: {} by user id: {}", collectionId, userId);

        RecipeCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(collectionId));

        if (!collection.getOwner().getId().equals(userId)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            if (user.getRole() != Role.ADMIN) {
                throw new UnauthorizedModificationException("You do not have permission to delete this collection");
            }
        }

        collectionRepository.delete(collection);
        log.info("Collection deleted successfully with id: {}", collectionId);
    }

    @Override
    public Page<CollectionSummaryResponse> getUserCollections(Long userId, Pageable pageable) {
        log.debug("Fetching collections for user id: {}", userId);

        return collectionRepository.findByOwnerId(userId, pageable)
                .map(collectionMapper::toSummaryResponse);
    }

    @Override
    public Page<CollectionSummaryResponse> getPublicCollections(Pageable pageable) {
        log.debug("Fetching public collections");

        return collectionRepository.findByVisibility(CollectionVisibility.PUBLIC, pageable)
                .map(collectionMapper::toSummaryResponse);
    }

    @Override
    public CollectionDetailResponse getCollectionById(Long userId, Long collectionId) {
        log.debug("Fetching collection id: {} for user id: {}", collectionId, userId);

        RecipeCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(collectionId));

        if (collection.getVisibility() == CollectionVisibility.PRIVATE) {
            if (!collection.getOwner().getId().equals(userId)) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
                if (user.getRole() != Role.ADMIN) {
                    throw new CollectionAccessDeniedException("You do not have access to this private collection");
                }
            }
        }

        return buildDetailResponse(collection);
    }

    @Override
    public CollectionDetailResponse getPublicCollectionById(Long collectionId) {
        log.debug("Fetching public collection id: {}", collectionId);

        RecipeCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(collectionId));

        if (collection.getVisibility() != CollectionVisibility.PUBLIC) {
            throw new CollectionAccessDeniedException("This collection is not public");
        }

        return buildDetailResponse(collection);
    }

    @Override
    @Transactional
    public CollectionRecipeResponse addRecipeToCollection(Long userId, Long collectionId, Long recipeId) {
        log.debug("Adding recipe id: {} to collection id: {} by user id: {}", recipeId, collectionId, userId);

        RecipeCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(collectionId));

        if (!collection.getOwner().getId().equals(userId)) {
            throw new UnauthorizedModificationException("You do not have permission to add recipes to this collection");
        }

        if (collectionRecipeRepository.existsByCollectionIdAndRecipeId(collectionId, recipeId)) {
            throw new DuplicateCollectionRecipeException();
        }

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException(recipeId));

        CollectionRecipe collectionRecipe = CollectionRecipe.builder()
                .collection(collection)
                .recipe(recipe)
                .build();

        CollectionRecipe saved = collectionRecipeRepository.save(collectionRecipe);
        log.info("Recipe id: {} added to collection id: {}", recipeId, collectionId);

        return collectionMapper.toRecipeResponse(saved);
    }

    @Override
    @Transactional
    public void removeRecipeFromCollection(Long userId, Long collectionId, Long recipeId) {
        log.debug("Removing recipe id: {} from collection id: {} by user id: {}", recipeId, collectionId, userId);

        RecipeCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(collectionId));

        if (!collection.getOwner().getId().equals(userId)) {
            throw new UnauthorizedModificationException("You do not have permission to remove recipes from this collection");
        }

        CollectionRecipe collectionRecipe = collectionRecipeRepository
                .findByCollectionIdAndRecipeId(collectionId, recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found in this collection"));

        collectionRecipeRepository.delete(collectionRecipe);
        log.info("Recipe id: {} removed from collection id: {}", recipeId, collectionId);
    }

    @Override
    public Page<CollectionRecipeResponse> getCollectionRecipes(Long userId, Long collectionId, Pageable pageable) {
        log.debug("Fetching recipes for collection id: {} by user id: {}", collectionId, userId);

        RecipeCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(collectionId));

        if (collection.getVisibility() == CollectionVisibility.PRIVATE) {
            if (!collection.getOwner().getId().equals(userId)) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
                if (user.getRole() != Role.ADMIN) {
                    throw new CollectionAccessDeniedException("You do not have access to this private collection");
                }
            }
        }

        return collectionRecipeRepository.findByCollectionId(collectionId, pageable)
                .map(collectionMapper::toRecipeResponse);
    }

    @Override
    public Page<CollectionRecipeResponse> getPublicCollectionRecipes(Long collectionId, Pageable pageable) {
        log.debug("Fetching recipes for public collection id: {}", collectionId);

        RecipeCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(collectionId));

        if (collection.getVisibility() != CollectionVisibility.PUBLIC) {
            throw new CollectionAccessDeniedException("This collection is not public");
        }

        return collectionRecipeRepository.findByCollectionId(collectionId, pageable)
                .map(collectionMapper::toRecipeResponse);
    }

    @Override
    public Page<CollectionSummaryResponse> searchCollections(String keyword, String ownerUsername, String visibility, Pageable pageable) {
        log.debug("Searching collections - keyword: {}, ownerUsername: {}, visibility: {}", keyword, ownerUsername, visibility);

        CollectionVisibility vis = null;
        if (visibility != null && !visibility.isBlank()) {
            vis = CollectionVisibility.valueOf(visibility.toUpperCase());
        }

        return collectionRepository.searchCollections(keyword, ownerUsername, vis, pageable)
                .map(collectionMapper::toSummaryResponse);
    }

    private CollectionDetailResponse buildDetailResponse(RecipeCollection collection) {
        var recipeResponses = collection.getCollectionRecipes().stream()
                .map(collectionMapper::toRecipeResponse)
                .toList();

        return new CollectionDetailResponse(
                collection.getId(),
                collection.getName(),
                collection.getDescription(),
                collection.getVisibility().name(),
                collection.getOwner().getUsername(),
                collection.getCreatedAt(),
                collection.getUpdatedAt(),
                recipeResponses,
                recipeResponses.size()
        );
    }
}
