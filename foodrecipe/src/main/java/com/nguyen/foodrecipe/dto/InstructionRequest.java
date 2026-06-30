package com.nguyen.foodrecipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for a single instruction step within a {@link RecipeRequest}.
 */
public record InstructionRequest(

        @NotNull(message = "Step number is required")
        Integer stepNumber,

        @NotBlank(message = "Instruction text cannot be blank")
        String instructionText
) {
}
