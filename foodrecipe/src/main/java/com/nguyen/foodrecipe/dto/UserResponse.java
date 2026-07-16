package com.nguyen.foodrecipe.dto;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role
) {}
