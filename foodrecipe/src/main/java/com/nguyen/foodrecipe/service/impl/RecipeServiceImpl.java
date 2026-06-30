package com.nguyen.foodrecipe.service.impl;

import com.nguyen.foodrecipe.dto.InstructionRequest;
import com.nguyen.foodrecipe.dto.RecipeRequest;
import com.nguyen.foodrecipe.dto.RecipeResponse;
import com.nguyen.foodrecipe.entity.Category;
import com.nguyen.foodrecipe.entity.Ingredient;
import com.nguyen.foodrecipe.entity.Instruction;
import com.nguyen.foodrecipe.entity.Recipe;
import com.nguyen.foodrecipe.exception.CategoryNotFoundException;
import com.nguyen.foodrecipe.exception.RecipeNotFoundException;
import com.nguyen.foodrecipe.mapper.RecipeMapper;
import com.nguyen.foodrecipe.repository.CategoryRepository;
import com.nguyen.foodrecipe.repository.RecipeRepository;
import com.nguyen.foodrecipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link RecipeService}.
 * <p>
 * Handles creation, update, and deletion of recipes together with their
 * child ingredients and instructions. Uses {@code orphanRemoval = true}
 * on the entity to automatically delete stale children on update.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;
    private final RecipeMapper recipeMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public RecipeResponse createRecipe(RecipeRequest request) {
        log.debug("Creating recipe with title: {}", request.title());

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        Recipe recipe = Recipe.builder()
                .title(request.title())
                .imageUrl(request.imageUrl())
                .description(request.description())
                .category(category)
                .build();

        addIngredientsToRecipe(recipe, request);
        addInstructionsToRecipe(recipe, request);

        Recipe saved = recipeRepository.save(recipe);
        log.info("Recipe created successfully with id: {}", saved.getId());

        return recipeMapper.toResponse(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public RecipeResponse updateRecipe(Long id, RecipeRequest request) {
        log.debug("Updating recipe id: {}", id);

        Recipe recipe = recipeRepository.findWithDetailsById(id)
                .orElseThrow(() -> new RecipeNotFoundException(id));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        recipe.setTitle(request.title());
        recipe.setImageUrl(request.imageUrl());
        recipe.setDescription(request.description());
        recipe.setCategory(category);

        // Clear existing children — orphanRemoval will delete them
        recipe.getIngredients().clear();
        recipe.getInstructions().clear();

        addIngredientsToRecipe(recipe, request);
        addInstructionsToRecipe(recipe, request);

        Recipe updated = recipeRepository.save(recipe);
        log.info("Recipe updated successfully with id: {}", updated.getId());

        return recipeMapper.toResponse(updated);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteRecipe(Long id) {
        log.debug("Deleting recipe id: {}", id);

        if (!recipeRepository.existsById(id)) {
            throw new RecipeNotFoundException(id);
        }

        recipeRepository.deleteById(id);
        log.info("Recipe deleted successfully with id: {}", id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecipeResponse getRecipeById(Long id) {
        log.debug("Fetching recipe id: {}", id);

        Recipe recipe = recipeRepository.findWithDetailsById(id)
                .orElseThrow(() -> new RecipeNotFoundException(id));

        return recipeMapper.toResponse(recipe);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<RecipeResponse> getAllRecipes(Pageable pageable) {
        log.debug("Fetching all recipes — page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        return recipeRepository.findAll(pageable)
                .map(recipeMapper::toResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<RecipeResponse> searchRecipes(String keyword, Pageable pageable) {
        log.debug("Searching recipes with keyword: {}", keyword);

        return recipeRepository.searchByTitle(keyword, pageable)
                .map(recipeMapper::toResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<RecipeResponse> getRecipesByCategory(Long categoryId, Pageable pageable) {
        log.debug("Fetching recipes for category id: {}", categoryId);

        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }

        return recipeRepository.findByCategoryId(categoryId, pageable)
                .map(recipeMapper::toResponse);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /**
     * Converts ingredient strings from the request into entities and
     * adds them to the recipe using the bidirectional helper.
     */
    private void addIngredientsToRecipe(Recipe recipe, RecipeRequest request) {
        for (String ingredientText : request.ingredients()) {
            Ingredient ingredient = Ingredient.builder()
                    .ingredientText(ingredientText)
                    .build();
            recipe.addIngredient(ingredient);
        }
    }

    /**
     * Converts instruction request objects into entities and
     * adds them to the recipe using the bidirectional helper.
     */
    private void addInstructionsToRecipe(Recipe recipe, RecipeRequest request) {
        for (InstructionRequest instrReq : request.instructions()) {
            Instruction instruction = Instruction.builder()
                    .stepNumber(instrReq.stepNumber())
                    .instructionText(instrReq.instructionText())
                    .build();
            recipe.addInstruction(instruction);
        }
    }
}
