package com.nguyen.foodrecipe.service;

import com.nguyen.foodrecipe.dto.FavoriteResponse;
import com.nguyen.foodrecipe.dto.RecipeSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {

    void addFavorite(Long userId, Long recipeId);

    void removeFavorite(Long userId, Long recipeId);

    Page<RecipeSummaryResponse> getUserFavorites(Long userId, Pageable pageable);
}
