package com.nguyen.foodrecipe.controller;

import com.nguyen.foodrecipe.audit.AdminAuditService;
import com.nguyen.foodrecipe.dto.*;
import com.nguyen.foodrecipe.security.UserPrincipal;
import com.nguyen.foodrecipe.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administrative APIs (requires ADMIN role)")
@SecurityRequirement(name = "bearer-jwt")
public class AdminController {

    private final RecipeService recipeService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final AdminService adminService;
    private final AdminAuditService auditService;

    // ── Recipe Management ──

    @GetMapping("/recipes")
    @Operation(summary = "List all recipes", description = "Get paginated list of all recipes. ADMIN only.")
    public ResponseEntity<ApiResponse<Page<RecipeSummaryResponse>>> getAllRecipes(
            @RequestParam(required = false) @Parameter(description = "Optional keyword filter") String keyword,
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        Page<RecipeSummaryResponse> page = recipeService.getAllRecipes(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("Recipes fetched successfully", page));
    }

    @GetMapping("/recipes/{id}")
    @Operation(summary = "Get recipe details", description = "Get complete recipe details including stats. ADMIN only.")
    public ResponseEntity<ApiResponse<RecipeDetailResponse>> getRecipeById(@PathVariable Long id) {
        RecipeDetailResponse response = recipeService.getRecipeById(id);
        return ResponseEntity.ok(ApiResponse.success("Recipe fetched successfully", response));
    }

    @PostMapping("/recipes")
    @Operation(summary = "Create recipe", description = "Create a new recipe. ADMIN only.")
    public ResponseEntity<ApiResponse<RecipeResponse>> createRecipe(
            @Valid @RequestBody RecipeRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        RecipeResponse response = recipeService.createRecipe(request);
        auditService.log(admin.getUsername(), admin.getId(), "RECIPE_CREATED", "Recipe",
                response.id(), "Title: " + request.title());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Recipe created successfully", response));
    }

    @PutMapping("/recipes/{id}")
    @Operation(summary = "Update recipe", description = "Update an existing recipe. ADMIN only.")
    public ResponseEntity<ApiResponse<RecipeResponse>> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody RecipeRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        RecipeResponse response = recipeService.updateRecipe(id, request);
        auditService.log(admin.getUsername(), admin.getId(), "RECIPE_UPDATED", "Recipe",
                id, "Title: " + request.title());
        return ResponseEntity.ok(ApiResponse.success("Recipe updated successfully", response));
    }

    @DeleteMapping("/recipes/{id}")
    @Operation(summary = "Delete recipe", description = "Permanently delete a recipe. ADMIN only.")
    public ResponseEntity<ApiResponse<Void>> deleteRecipe(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal admin) {
        recipeService.deleteRecipe(id);
        auditService.log(admin.getUsername(), admin.getId(), "RECIPE_DELETED", "Recipe", id, null);
        return ResponseEntity.ok(ApiResponse.success("Recipe deleted successfully"));
    }

    // ── Category Management ──

    @GetMapping("/categories")
    @Operation(summary = "List all categories", description = "Get all categories. ADMIN only.")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Categories fetched successfully", categories));
    }

    @GetMapping("/categories/{id}")
    @Operation(summary = "Get category", description = "Get a single category by ID. ADMIN only.")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Category fetched successfully", response));
    }

    @PostMapping("/categories")
    @Operation(summary = "Create category", description = "Create a new category. ADMIN only.")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        CategoryResponse response = categoryService.createCategory(request);
        auditService.log(admin.getUsername(), admin.getId(), "CATEGORY_CREATED", "Category",
                response.id(), "Name: " + request.name());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", response));
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "Update category", description = "Update an existing category. ADMIN only.")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        auditService.log(admin.getUsername(), admin.getId(), "CATEGORY_UPDATED", "Category",
                id, "Name: " + request.name());
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", response));
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Delete category", description = "Delete a category. Fails if category still contains recipes. ADMIN only.")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal admin) {
        categoryService.deleteCategory(id);
        auditService.log(admin.getUsername(), admin.getId(), "CATEGORY_DELETED", "Category", id, null);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }

    // ── User Management ──

    @GetMapping("/users")
    @Operation(summary = "List users", description = "Get paginated list of users, optionally filtered by keyword. ADMIN only.")
    public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> getAllUsers(
            @RequestParam(required = false) @Parameter(description = "Search by username or email") String keyword,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<AdminUserResponse> page = adminService.getAllUsers(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("Users fetched successfully", page));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user", description = "Get a single user by ID. ADMIN only.")
    public ResponseEntity<ApiResponse<AdminUserResponse>> getUserById(@PathVariable Long id) {
        AdminUserResponse response = adminService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User fetched successfully", response));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update user", description = "Update a user's username and/or email. ADMIN only.")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUser(
            @PathVariable Long id,
            @RequestBody AdminUserUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        AdminUserResponse response = adminService.updateUser(id, request.username(), request.email());
        auditService.log(admin.getUsername(), admin.getId(), "USER_UPDATED", "User", id,
                "username=" + request.username() + ", email=" + request.email());
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
    }

    @PatchMapping("/users/{id}/enable")
    @Operation(summary = "Enable user", description = "Enable a disabled user. ADMIN only.")
    public ResponseEntity<ApiResponse<Void>> enableUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal admin) {
        adminService.toggleUserEnabled(id, true, admin.getId(), admin.getUsername());
        return ResponseEntity.ok(ApiResponse.success("User enabled successfully"));
    }

    @PatchMapping("/users/{id}/disable")
    @Operation(summary = "Disable user", description = "Disable a user. Cannot disable the last active admin. ADMIN only.")
    public ResponseEntity<ApiResponse<Void>> disableUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal admin) {
        adminService.toggleUserEnabled(id, false, admin.getId(), admin.getUsername());
        return ResponseEntity.ok(ApiResponse.success("User disabled successfully"));
    }

    @PatchMapping("/users/{id}/role")
    @Operation(summary = "Change user role", description = "Change a user's role. Admins cannot remove their own ADMIN role. ADMIN only.")
    public ResponseEntity<ApiResponse<Void>> changeUserRole(
            @PathVariable Long id,
            @RequestBody RoleChangeRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        adminService.changeUserRole(id, request.role(), admin.getId(), admin.getUsername());
        return ResponseEntity.ok(ApiResponse.success("User role changed successfully"));
    }

    // ── Comment Moderation ──

    @GetMapping("/comments")
    @Operation(summary = "List comments", description = "Get paginated list of all comments, optionally filtered by keyword (matches username, recipe title, or content). ADMIN only.")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getAllComments(
            @RequestParam(required = false) @Parameter(description = "Search by username, recipe title, or content") String keyword,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<CommentResponse> page = commentService.getAllComments(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("Comments fetched successfully", page));
    }

    @DeleteMapping("/comments/{id}")
    @Operation(summary = "Delete comment", description = "Permanently delete any comment. ADMIN only.")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal admin) {
        commentService.adminDeleteComment(id);
        auditService.log(admin.getUsername(), admin.getId(), "COMMENT_DELETED", "Comment", id, null);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully"));
    }

    // ── Dashboard ──

    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard statistics", description = "Get aggregate platform statistics. ADMIN only.")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        DashboardResponse response = adminService.getDashboard();
        return ResponseEntity.ok(ApiResponse.success("Dashboard data fetched successfully", response));
    }
}
