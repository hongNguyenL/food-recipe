package com.nguyen.foodrecipe.controller;

import com.nguyen.foodrecipe.dto.AuthResponse;
import com.nguyen.foodrecipe.dto.LoginRequest;
import com.nguyen.foodrecipe.dto.RegisterRequest;
import com.nguyen.foodrecipe.dto.UserResponse;
import com.nguyen.foodrecipe.exception.DuplicateEmailException;
import com.nguyen.foodrecipe.exception.DuplicateUsernameException;
import com.nguyen.foodrecipe.security.CustomUserDetailsService;
import com.nguyen.foodrecipe.security.JwtService;
import com.nguyen.foodrecipe.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        AuthService authService() {
            return mock(AuthService.class);
        }

        @Bean
        JwtService jwtService() {
            return mock(JwtService.class);
        }

        @Bean
        CustomUserDetailsService userDetailsService() {
            return mock(CustomUserDetailsService.class);
        }
    }

    @Test
    void register_ShouldReturn201() throws Exception {
        UserResponse response = new UserResponse(1L, "testuser", "test@example.com", "USER");

        given(authService.register(any(RegisterRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"testuser","email":"test@example.com","password":"Password123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    void register_DuplicateUsername_ShouldReturn409WithFieldError() throws Exception {
        given(authService.register(any(RegisterRequest.class)))
                .willThrow(new DuplicateUsernameException("testuser"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"testuser","email":"test@example.com","password":"Password123"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors.username").value("Username already taken: testuser"));
    }

    @Test
    void register_DuplicateEmail_ShouldReturn409WithFieldError() throws Exception {
        given(authService.register(any(RegisterRequest.class)))
                .willThrow(new DuplicateEmailException("test@example.com"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"testuser","email":"test@example.com","password":"Password123"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors.email").value("Email already in use: test@example.com"));
    }

    @Test
    void register_InvalidPassword_ShouldReturn400WithFieldError() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"testuser","email":"test@example.com","password":"weak"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("password:")))
                .andExpect(jsonPath("$.errors.password").value(
                        "Password must be at least 8 characters and contain uppercase, lowercase, and a digit"));
    }

    @Test
    void register_EmptyFields_ShouldReturn400WithAllFieldErrors() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"","email":"","password":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("username:")))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("email:")))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("password:")))
                .andExpect(jsonPath("$.errors.username").value("Username is required"))
                .andExpect(jsonPath("$.errors.email").value("Email is required"))
                .andExpect(jsonPath("$.errors.password").value("Password is required"));
    }

    @Test
    void register_ShortUsername_ShouldReturnFieldError() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"ab","email":"test@example.com","password":"Password123"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors.username").value(
                        "Username must be between 3 and 30 characters"));
    }

    @Test
    void login_ShouldReturn200WithToken() throws Exception {
        AuthResponse authResponse = new AuthResponse("jwt-token", "Bearer", 86400L);

        given(authService.login(any(LoginRequest.class))).willReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"usernameOrEmail":"testuser","password":"Password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(86400));
    }

    @Test
    void login_InvalidCredentials_ShouldReturn401() throws Exception {
        given(authService.login(any(LoginRequest.class)))
                .willThrow(new BadCredentialsException("bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"usernameOrEmail":"wrong","password":"wrong"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
