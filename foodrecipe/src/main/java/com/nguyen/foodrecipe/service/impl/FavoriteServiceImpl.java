package com.nguyen.foodrecipe.service.impl;

import com.nguyen.foodrecipe.dto.RecipeSummaryResponse;
import com.nguyen.foodrecipe.entity.Favorite;
import com.nguyen.foodrecipe.entity.Recipe;
import com.nguyen.foodrecipe.entity.User;
import com.nguyen.foodrecipe.exception.DuplicateFavoriteException;
import com.nguyen.foodrecipe.exception.RecipeNotFoundException;
import com.nguyen.foodrecipe.mapper.RecipeMapper;
import com.nguyen.foodrecipe.repository.FavoriteRepository;
import com.nguyen.foodrecipe.repository.RecipeRepository;
import com.nguyen.foodrecipe.repository.UserRepository;
import com.nguyen.foodrecipe.service.FavoriteService;
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
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeMapper recipeMapper;

    @Override
    @Transactional
    public void addFavorite(Long userId, Long recipeId) {
        log.debug("Adding favorite: user={}, recipe={}", userId, recipeId);
        if (favoriteRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            throw new DuplicateFavoriteException(recipeId);
        }
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException(recipeId));
        User user = userRepository.getReferenceById(userId);
        Favorite favorite = Favorite.builder().user(user).recipe(recipe).build();
        favoriteRepository.save(favorite);
        log.info("Favorite added: user={}, recipe={}", userId, recipeId);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long recipeId) {
        log.debug("Removing favorite: user={}, recipe={}", userId, recipeId);
        favoriteRepository.deleteByUserIdAndRecipeId(userId, recipeId);
        log.info("Favorite removed: user={}, recipe={}", userId, recipeId);
    }

    @Override
    public Page<RecipeSummaryResponse> getUserFavorites(Long userId, Pageable pageable) {
        log.debug("Fetching favorites for user: {}", userId);
        return favoriteRepository.findByUserId(userId, pageable)
                .map(fav -> recipeMapper.toSummaryResponse(fav.getRecipe()));
    }
}
