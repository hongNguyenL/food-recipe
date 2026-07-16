package com.nguyen.foodrecipe.service;

import com.nguyen.foodrecipe.dto.AuthResponse;
import com.nguyen.foodrecipe.dto.LoginRequest;
import com.nguyen.foodrecipe.dto.RegisterRequest;
import com.nguyen.foodrecipe.dto.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
