package com.nguyen.foodrecipe.service.impl;

import com.nguyen.foodrecipe.dto.IngredientResponse;
import com.nguyen.foodrecipe.dto.InstructionRequest;
import com.nguyen.foodrecipe.dto.InstructionResponse;
import com.nguyen.foodrecipe.dto.RecipeDetailResponse;
import com.nguyen.foodrecipe.dto.RecipeRequest;
import com.nguyen.foodrecipe.dto.RecipeResponse;
import com.nguyen.foodrecipe.dto.RecipeSummaryResponse;
import com.nguyen.foodrecipe.entity.Category;
import com.nguyen.foodrecipe.entity.Ingredient;
import com.nguyen.foodrecipe.entity.Instruction;
import com.nguyen.foodrecipe.entity.Recipe;
import com.nguyen.foodrecipe.exception.CategoryNotFoundException;
import com.nguyen.foodrecipe.exception.RecipeNotFoundException;
import com.nguyen.foodrecipe.mapper.CategoryMapper;
import com.nguyen.foodrecipe.mapper.RecipeMapper;
import com.nguyen.foodrecipe.repository.CategoryRepository;
import com.nguyen.foodrecipe.repository.CommentRepository;
import com.nguyen.foodrecipe.repository.FavoriteRepository;
import com.nguyen.foodrecipe.repository.RatingRepository;
import com.nguyen.foodrecipe.repository.RecipeRepository;
import com.nguyen.foodrecipe.service.RecipeService;
import java.util.List;
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
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;
    private final FavoriteRepository favoriteRepository;
    private final RatingRepository ratingRepository;
    private final CommentRepository commentRepository;
    private final RecipeMapper recipeMapper;
    private final CategoryMapper categoryMapper;

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

        recipe.getIngredients().clear();
        recipe.getInstructions().clear();

        addIngredientsToRecipe(recipe, request);
        addInstructionsToRecipe(recipe, request);

        Recipe updated = recipeRepository.save(recipe);
        log.info("Recipe updated successfully with id: {}", updated.getId());

        return recipeMapper.toResponse(updated);
    }

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

    @Override
    public RecipeDetailResponse getRecipeById(Long id) {
        log.debug("Fetching recipe id: {}", id);

        Recipe recipe = recipeRepository.findWithDetailsById(id)
                .orElseThrow(() -> new RecipeNotFoundException(id));

        long favoriteCount = favoriteRepository.countByRecipeId(id);
        Double avg = ratingRepository.findAverageRatingByRecipeId(id);
        double averageRating = (avg != null) ? Math.round(avg * 10.0) / 10.0 : 0.0;
        long totalRatings = ratingRepository.countByRecipeId(id);
        long totalComments = commentRepository.countByRecipeId(id);

        List<IngredientResponse> ingredientResponses = recipe.getIngredients().stream()
                .map(recipeMapper::toIngredientResponse).toList();
        List<InstructionResponse> instructionResponses = recipe.getInstructions().stream()
                .map(recipeMapper::toInstructionResponse).toList();

        return new RecipeDetailResponse(
                recipe.getId(), recipe.getTitle(), recipe.getImageUrl(),
                recipe.getDescription(),
                categoryMapper.toResponse(recipe.getCategory()),
                ingredientResponses, instructionResponses,
                favoriteCount, averageRating, totalRatings, totalComments
        );
    }

    @Override
    public Page<RecipeSummaryResponse> getAllRecipes(String keyword, Pageable pageable) {
        log.debug("Fetching all recipes — page: {}, size: {}, keyword: {}",
                pageable.getPageNumber(), pageable.getPageSize(), keyword);

        if (keyword != null && !keyword.isBlank()) {
            return recipeRepository.searchByTitle(keyword.trim(), pageable)
                    .map(recipeMapper::toSummaryResponse);
        }

        return recipeRepository.findAll(pageable)
                .map(recipeMapper::toSummaryResponse);
    }

    @Override
    public Page<RecipeSummaryResponse> searchRecipes(String keyword, Pageable pageable) {
        log.debug("Searching recipes with keyword: {}", keyword);

        return recipeRepository.searchByTitle(keyword.trim(), pageable)
                .map(recipeMapper::toSummaryResponse);
    }

    @Override
    public Page<RecipeSummaryResponse> getRecipesByCategory(Long categoryId, Pageable pageable) {
        log.debug("Fetching recipes for category id: {}", categoryId);

        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }

        return recipeRepository.findByCategoryId(categoryId, pageable)
                .map(recipeMapper::toSummaryResponse);
    }

    private void addIngredientsToRecipe(Recipe recipe, RecipeRequest request) {
        for (String ingredientText : request.ingredients()) {
            Ingredient ingredient = Ingredient.builder()
                    .ingredientText(ingredientText)
                    .build();
            recipe.addIngredient(ingredient);
        }
    }

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
