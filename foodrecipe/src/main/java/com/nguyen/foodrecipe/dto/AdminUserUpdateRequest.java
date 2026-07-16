package com.nguyen.foodrecipe.dto;

public record AdminUserUpdateRequest(
    String username,
    String email
) {}
