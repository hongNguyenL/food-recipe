package com.nguyen.foodrecipe.dto;

/**
 * Response DTO for a {@link com.nguyen.foodrecipe.entity.Category}.
 */
public record CategoryResponse(
        Long id,
        String name
) {
}
