package com.nguyen.foodrecipe.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CollectionDetailResponse(
        Long id,
        String name,
        String description,
        String visibility,
        String ownerUsername,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<CollectionRecipeResponse> recipes,
        int totalRecipeCount
) {}
