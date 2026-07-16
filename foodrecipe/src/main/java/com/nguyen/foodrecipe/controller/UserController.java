package com.nguyen.foodrecipe.controller;

import com.nguyen.foodrecipe.dto.ApiResponse;
import com.nguyen.foodrecipe.dto.RecipeSummaryResponse;
import com.nguyen.foodrecipe.dto.UserResponse;
import com.nguyen.foodrecipe.security.UserPrincipal;
import com.nguyen.foodrecipe.service.FavoriteService;
import com.nguyen.foodrecipe.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User profile APIs")
public class UserController {

    private final UserService userService;
    private final FavoriteService favoriteService;

    @GetMapping("/me")
    @Operation(summary = "Get current user",
            description = "Retrieve the authenticated user's profile information")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserResponse response = userService.getCurrentUser(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("User fetched successfully", response));
    }

    @GetMapping("/me/favorites")
    @Operation(summary = "Get user favorites",
            description = "Retrieve the authenticated user's favorite recipes as a paginated list")
    public ResponseEntity<ApiResponse<Page<RecipeSummaryResponse>>> getUserFavorites(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<RecipeSummaryResponse> page = favoriteService.getUserFavorites(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Favorites fetched successfully", page));
    }
}
