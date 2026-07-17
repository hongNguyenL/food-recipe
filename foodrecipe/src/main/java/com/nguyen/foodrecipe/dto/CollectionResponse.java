package com.nguyen.foodrecipe.dto;

import java.time.LocalDateTime;

public record CollectionResponse(
        Long id,
        String name,
        String description,
        String visibility,
        String ownerUsername,
        int recipeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
