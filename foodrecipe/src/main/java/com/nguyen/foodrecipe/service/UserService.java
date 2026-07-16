package com.nguyen.foodrecipe.service;

import com.nguyen.foodrecipe.dto.UserResponse;

public interface UserService {

    UserResponse getCurrentUser(Long userId);
}
