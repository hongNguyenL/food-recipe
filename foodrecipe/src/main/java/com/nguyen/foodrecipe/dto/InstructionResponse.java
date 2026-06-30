package com.nguyen.foodrecipe.dto;

/**
 * Response DTO for a {@link com.nguyen.foodrecipe.entity.Instruction}.
 */
public record InstructionResponse(
        Long id,
        Integer stepNumber,
        String instructionText
) {
}
