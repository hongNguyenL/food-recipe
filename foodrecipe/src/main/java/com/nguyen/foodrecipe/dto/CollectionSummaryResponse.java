package com.nguyen.foodrecipe.dto;

import java.time.LocalDateTime;

public record CollectionSummaryResponse(
        Long id,
        String name,
        String description,
        String visibility,
        String ownerUsername,
        int recipeCount,
        LocalDateTime createdAt
) {}
