package com.nguyen.foodrecipe.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}
