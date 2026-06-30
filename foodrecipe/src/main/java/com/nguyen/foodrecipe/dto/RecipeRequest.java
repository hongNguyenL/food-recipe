package com.nguyen.foodrecipe.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request DTO for creating or updating a {@link com.nguyen.foodrecipe.entity.Recipe}.
 * <p>
 * Ingredients are supplied as plain text strings; instructions carry
 * a step number and text. Both collections cascade through the recipe.
 * </p>
 */
public record RecipeRequest(

        @NotBlank(message = "Recipe title is required")
        @Size(max = 200, message = "Recipe title must not exceed 200 characters")
        String title,

        String imageUrl,

        String description,

        @NotNull(message = "Category ID is required")
        Long categoryId,

        @NotEmpty(message = "At least one ingredient is required")
        List<@NotBlank(message = "Ingredient text cannot be blank") String> ingredients,

        @NotEmpty(message = "At least one instruction is required")
        List<@Valid InstructionRequest> instructions
) {
}
