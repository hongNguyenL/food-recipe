package com.nguyen.foodrecipe.service;

import com.nguyen.foodrecipe.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {

    Page<AdminUserResponse> getAllUsers(String keyword, Pageable pageable);

    AdminUserResponse getUserById(Long id);

    AdminUserResponse updateUser(Long id, String username, String email);

    void toggleUserEnabled(Long id, boolean enabled, Long adminId, String adminUsername);

    void changeUserRole(Long id, String newRole, Long adminId, String adminUsername);

    DashboardResponse getDashboard();
}
