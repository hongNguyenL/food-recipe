package com.nguyen.foodrecipe.service.impl;

import com.nguyen.foodrecipe.dto.IngredientResponse;
import com.nguyen.foodrecipe.dto.InstructionRequest;
import com.nguyen.foodrecipe.dto.InstructionResponse;
import com.nguyen.foodrecipe.dto.PantrySearchRequest;
import com.nguyen.foodrecipe.dto.PantrySearchResult;
import com.nguyen.foodrecipe.dto.PopularRecipeResponse;
import com.nguyen.foodrecipe.dto.RecipeDetailResponse;
import com.nguyen.foodrecipe.dto.RecipeRequest;
import com.nguyen.foodrecipe.dto.RecipeResponse;
import com.nguyen.foodrecipe.dto.RecipeSummaryResponse;
import com.nguyen.foodrecipe.dto.SearchRecipeResponse;
import com.nguyen.foodrecipe.dto.SimilarRecipeResponse;
import com.nguyen.foodrecipe.entity.Category;
import com.nguyen.foodrecipe.entity.Ingredient;
import com.nguyen.foodrecipe.entity.Instruction;
import com.nguyen.foodrecipe.entity.Recipe;
import com.nguyen.foodrecipe.exception.CategoryNotFoundException;
import com.nguyen.foodrecipe.exception.InvalidPageSizeException;
import com.nguyen.foodrecipe.exception.RecipeNotFoundException;
import com.nguyen.foodrecipe.mapper.CategoryMapper;
import com.nguyen.foodrecipe.mapper.RecipeMapper;
import com.nguyen.foodrecipe.repository.CategoryRepository;
import com.nguyen.foodrecipe.repository.CommentRepository;
import com.nguyen.foodrecipe.repository.FavoriteRepository;
import com.nguyen.foodrecipe.repository.RatingRepository;
import com.nguyen.foodrecipe.repository.RecipeRepository;
import com.nguyen.foodrecipe.service.RecipeService;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @CacheEvict(value = {"recipeDetails", "recipeSummaries", "popularRecipes", "topRatedRecipes"}, allEntries = true)
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
    @CacheEvict(value = {"recipeDetails", "recipeSummaries", "popularRecipes", "topRatedRecipes"}, allEntries = true)
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
    @CacheEvict(value = {"recipeDetails", "recipeSummaries", "popularRecipes", "topRatedRecipes"}, allEntries = true)
    public void deleteRecipe(Long id) {
        log.debug("Deleting recipe id: {}", id);

        if (!recipeRepository.existsById(id)) {
            throw new RecipeNotFoundException(id);
        }

        recipeRepository.deleteById(id);
        log.info("Recipe deleted successfully with id: {}", id);
    }

    @Override
    @Cacheable(value = "recipeDetails", key = "#id")
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

    @Override
    public Page<SearchRecipeResponse> advancedSearch(String keyword, Long categoryId, String ingredient, Pageable pageable) {
        log.debug("Advanced search — keyword: {}, categoryId: {}, ingredient: {}", keyword, categoryId, ingredient);

        if (pageable.getPageSize() > 100) {
            throw new InvalidPageSizeException("Page size must not exceed 100");
        }

        Sort mappedSort = mapSort(pageable.getSort());
        Pageable mappedPageable = PageRequest.of(
                pageable.getPageNumber(), pageable.getPageSize(), mappedSort);

        Page<Object[]> page = recipeRepository.searchRecipesNative(
                keyword, categoryId, ingredient, mappedPageable);

        List<SearchRecipeResponse> responses = page.getContent().stream()
                .map(this::toSearchResponse)
                .toList();

        return new PageImpl<>(responses, mappedPageable, page.getTotalElements());
    }

    @Override
    @Cacheable(value = "popularRecipes", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<PopularRecipeResponse> getPopularRecipes(Pageable pageable) {
        log.debug("Fetching popular recipes — page: {}", pageable.getPageNumber());

        Page<Object[]> page = recipeRepository.findPopularRecipes(pageable);

        List<PopularRecipeResponse> responses = page.getContent().stream()
                .map(this::toPopularResponse)
                .toList();

        return new PageImpl<>(responses, pageable, page.getTotalElements());
    }

    @Override
    public Page<SearchRecipeResponse> getTopRatedRecipes(Pageable pageable) {
        log.debug("Fetching top-rated recipes — page: {}", pageable.getPageNumber());

        Page<Object[]> page = recipeRepository.findTopRatedRecipes(pageable);

        List<SearchRecipeResponse> responses = page.getContent().stream()
                .map(this::toSearchResponse)
                .toList();

        return new PageImpl<>(responses, pageable, page.getTotalElements());
    }

    @Override
    public Page<RecipeSummaryResponse> getLatestRecipes(Pageable pageable) {
        log.debug("Fetching latest recipes — page: {}", pageable.getPageNumber());

        Pageable sortedByNewest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return recipeRepository.findAll(sortedByNewest)
                .map(recipeMapper::toSummaryResponse);
    }

    @Override
    public List<SimilarRecipeResponse> getSimilarRecipes(Long recipeId) {
        log.debug("Fetching similar recipes for recipe id: {}", recipeId);

        Recipe recipe = recipeRepository.findWithDetailsById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException(recipeId));

        List<Object[]> rows = recipeRepository.findSimilarRecipes(
                recipeId, recipe.getCategory().getId(), PageRequest.of(0, 10));

        return rows.stream().map(this::toSimilarResponse).toList();
    }

    @Override
    public Page<PantrySearchResult> pantrySearch(PantrySearchRequest request) {
        log.debug("Pantry search with {} ingredients", request.ingredients().size());

        if (request.size() > 100) {
            throw new InvalidPageSizeException("Page size must not exceed 100");
        }

        List<String> cleaned = request.ingredients().stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();

        String[] ingredients = cleaned.toArray(new String[0]);
        int appliedMinMatch = request.minMatchPercentage() != null
                ? request.minMatchPercentage()
                : 70;
        Pageable pageable = PageRequest.of(request.page(), request.size());

        Page<Object[]> page = recipeRepository.pantrySearch(
                ingredients, appliedMinMatch, request.categoryId(), pageable);

        List<PantrySearchResult> results = page.getContent().stream()
                .map(row -> toPantryResult(row, appliedMinMatch))
                .toList();

        return new PageImpl<>(results, pageable, page.getTotalElements());
    }

    private SearchRecipeResponse toSearchResponse(Object[] row) {
        Long id = toLong(row[0]);
        String title = (String) row[1];
        String imageUrl = (String) row[2];
        String categoryName = (String) row[3];
        LocalDateTime createdAt = row[4] instanceof Timestamp ts ? ts.toLocalDateTime() : null;
        double averageRating = toDouble(row[5]);
        long favoriteCount = toLong(row[6]);
        long commentCount = toLong(row[7]);

        return new SearchRecipeResponse(id, title, imageUrl, categoryName,
                createdAt, averageRating, favoriteCount, commentCount);
    }

    private PopularRecipeResponse toPopularResponse(Object[] row) {
        Long id = toLong(row[0]);
        String title = (String) row[1];
        String imageUrl = (String) row[2];
        String categoryName = (String) row[3];
        double averageRating = toDouble(row[4]);
        long favoriteCount = toLong(row[5]);
        long commentCount = toLong(row[6]);
        double popularityScore = toDouble(row[7]);

        return new PopularRecipeResponse(id, title, imageUrl, categoryName,
                averageRating, favoriteCount, commentCount, popularityScore);
    }

    private SimilarRecipeResponse toSimilarResponse(Object[] row) {
        Long id = toLong(row[0]);
        String title = (String) row[1];
        String imageUrl = (String) row[2];
        String categoryName = (String) row[3];
        double averageRating = toDouble(row[4]);

        return new SimilarRecipeResponse(id, title, imageUrl, categoryName, averageRating);
    }

    @SuppressWarnings("unchecked")
    private PantrySearchResult toPantryResult(Object[] row, int appliedMinMatch) {
        Long id = toLong(row[0]);
        String title = (String) row[1];
        String imageUrl = (String) row[2];
        String categoryName = (String) row[3];
        double averageRating = toDouble(row[4]);
        int matchedCount = (int) toLong(row[5]);
        int missingCount = (int) toLong(row[6]);
        int totalCount = (int) toLong(row[7]);
        int matchPercentage = (int) toLong(row[8]);

        String[] matchedArr = (String[]) row[9];
        String[] missingArr = (String[]) row[10];

        List<String> matched = matchedArr != null ? List.of(matchedArr) : List.of();
        List<String> missing = missingArr != null ? List.of(missingArr) : List.of();

        return new PantrySearchResult(id, title, imageUrl, categoryName, averageRating,
                matchPercentage, matched, missing, matchedCount, missingCount, totalCount,
                appliedMinMatch);
    }

    private static long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Number n) return n.longValue();
        return Long.parseLong(value.toString());
    }

    private static double toDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number n) return n.doubleValue();
        return Double.parseDouble(value.toString());
    }

    private static Sort mapSort(Sort sort) {
        if (sort == null || sort.isUnsorted()) return Sort.unsorted();
        List<Sort.Order> mapped = new ArrayList<>();
        for (Sort.Order order : sort) {
            String property = switch (order.getProperty()) {
                case "createdAt" -> "created_at";
                case "averageRating" -> "avg_rating";
                case "favoriteCount" -> "fav_count";
                case "commentCount" -> "com_count";
                default -> order.getProperty();
            };
            mapped.add(new Sort.Order(order.getDirection(), property));
        }
        return Sort.by(mapped);
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
