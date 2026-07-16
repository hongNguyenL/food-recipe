package com.nguyen.foodrecipe.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long recipeId,
        Long userId,
        String username,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
