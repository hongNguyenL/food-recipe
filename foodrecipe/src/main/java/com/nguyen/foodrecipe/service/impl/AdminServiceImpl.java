package com.nguyen.foodrecipe.service.impl;

import com.nguyen.foodrecipe.audit.AdminAuditService;
import com.nguyen.foodrecipe.dto.*;
import com.nguyen.foodrecipe.entity.Comment;
import com.nguyen.foodrecipe.entity.Favorite;
import com.nguyen.foodrecipe.entity.Rating;
import com.nguyen.foodrecipe.entity.Recipe;
import com.nguyen.foodrecipe.entity.Role;
import com.nguyen.foodrecipe.entity.User;
import com.nguyen.foodrecipe.exception.CannotDisableLastAdminException;
import com.nguyen.foodrecipe.exception.DuplicateEmailException;
import com.nguyen.foodrecipe.exception.DuplicateUsernameException;
import com.nguyen.foodrecipe.exception.SelfRoleRemovalException;
import com.nguyen.foodrecipe.mapper.RecipeMapper;
import com.nguyen.foodrecipe.mapper.UserMapper;
import com.nguyen.foodrecipe.repository.*;
import com.nguyen.foodrecipe.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;
    private final FavoriteRepository favoriteRepository;
    private final RatingRepository ratingRepository;
    private final CommentRepository commentRepository;
    private final UserMapper userMapper;
    private final RecipeMapper recipeMapper;
    private final AdminAuditService auditService;

    @Override
    public Page<AdminUserResponse> getAllUsers(String keyword, Pageable pageable) {
        Page<User> users;
        if (keyword != null && !keyword.isBlank()) {
            users = userRepository.searchUsers(keyword.trim(), pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        return users.map(this::toAdminUserResponse);
    }

    @Override
    public AdminUserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return toAdminUserResponse(user);
    }

    @Override
    @Transactional
    public AdminUserResponse updateUser(Long id, String username, String email) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (username != null && !username.equals(user.getUsername())) {
            if (userRepository.existsByUsername(username)) {
                throw new DuplicateUsernameException(username);
            }
            user.setUsername(username);
        }

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new DuplicateEmailException(email);
            }
            user.setEmail(email);
        }

        User saved = userRepository.save(user);
        log.info("Admin updated user: id={}, username={}, email={}", id, saved.getUsername(), saved.getEmail());
        return toAdminUserResponse(saved);
    }

    @Override
    @Transactional
    public void toggleUserEnabled(Long id, boolean enabled, Long adminId, String adminUsername) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (!enabled && user.getRole() == Role.ADMIN && user.isEnabled()) {
            long activeAdmins = userRepository.countByEnabledTrueAndRole(Role.ADMIN);
            if (activeAdmins <= 1) {
                throw new CannotDisableLastAdminException();
            }
        }

        user.setEnabled(enabled);
        userRepository.save(user);

        String action = enabled ? "USER_ENABLED" : "USER_DISABLED";
        auditService.log(adminUsername, adminId, action, "User", id,
                "User " + user.getUsername() + (enabled ? " enabled" : " disabled"));
        log.info("Admin {} user: id={}, username={}", enabled ? "enabled" : "disabled", id, user.getUsername());
    }

    @Override
    @Transactional
    public void changeUserRole(Long id, String newRole, Long adminId, String adminUsername) {
        if (id.equals(adminId)) {
            throw new SelfRoleRemovalException();
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        Role role;
        try {
            role = Role.valueOf(newRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + newRole + ". Valid roles: USER, ADMIN");
        }

        if (user.getRole() == Role.ADMIN && role != Role.ADMIN) {
            long activeAdmins = userRepository.countByEnabledTrueAndRole(Role.ADMIN);
            if (activeAdmins <= 1) {
                throw new CannotDisableLastAdminException();
            }
        }

        user.setRole(role);
        userRepository.save(user);

        auditService.log(adminUsername, adminId, "USER_ROLE_CHANGED", "User", id,
                "Role changed from " + user.getRole() + " to " + role);
        log.info("Admin changed user role: id={}, newRole={}", id, role);
    }

    @Override
    public DashboardResponse getDashboard() {
        long totalRecipes = recipeRepository.count();
        long totalUsers = userRepository.count();
        long totalCategories = categoryRepository.count();
        long totalFavorites = favoriteRepository.count();
        long totalRatings = ratingRepository.count();
        long totalComments = commentRepository.count();
        double averageRating = ratingRepository.findGlobalAverageRating().orElse(0.0);

        List<User> newestUserEntities = userRepository.findTop5ByOrderByCreatedAtDesc();
        List<AdminUserResponse> newestUsers = newestUserEntities.stream()
                .map(this::toAdminUserResponse)
                .toList();

        Page<Recipe> newestRecipePage = recipeRepository.findAll(
                PageRequest.of(0, 5, org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "createdAt")));
        List<RecipeSummaryResponse> newestRecipes = newestRecipePage.getContent().stream()
                .map(recipeMapper::toSummaryResponse)
                .toList();

        return new DashboardResponse(totalRecipes, totalUsers, totalCategories,
                totalFavorites, totalRatings, totalComments,
                Math.round(averageRating * 10.0) / 10.0,
                newestUsers, newestRecipes);
    }

    private AdminUserResponse toAdminUserResponse(User user) {
        return new AdminUserResponse(
                user.getId(), user.getUsername(), user.getEmail(),
                user.getRole().name(), user.isEnabled(),
                user.getCreatedAt(), user.getUpdatedAt());
    }
}
