package com.nguyen.foodrecipe.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleChangeRequest(
    @NotBlank(message = "Role is required")
    String role
) {}
