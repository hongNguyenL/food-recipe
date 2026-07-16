package com.nguyen.foodrecipe.dto;

import java.time.LocalDateTime;

public record FavoriteResponse(
        Long id,
        Long recipeId,
        LocalDateTime createdAt
) {}
