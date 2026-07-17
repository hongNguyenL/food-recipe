package com.nguyen.foodrecipe.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PantrySearchRequest(
        @NotEmpty(message = "At least one ingredient is required")
        @Size(max = 30, message = "Maximum 30 ingredients allowed")
        List<@Size(min = 1, message = "Ingredient name cannot be blank") String> ingredients,

        @Min(0) int page,

        @Min(1) @Max(100) int size,

        @Min(0) @Max(100) Integer minMatchPercentage,

        Long categoryId
) {}
