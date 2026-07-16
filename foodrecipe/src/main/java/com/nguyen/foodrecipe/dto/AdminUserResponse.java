package com.nguyen.foodrecipe.dto;

import java.time.LocalDateTime;

public record AdminUserResponse(
    Long id,
    String username,
    String email,
    String role,
    boolean enabled,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
