package com.nguyen.foodrecipe.controller;

import com.nguyen.foodrecipe.config.SecurityConfig;
import com.nguyen.foodrecipe.dto.UserResponse;
import com.nguyen.foodrecipe.entity.Role;
import com.nguyen.foodrecipe.entity.User;
import com.nguyen.foodrecipe.security.CustomUserDetailsService;
import com.nguyen.foodrecipe.security.JwtAuthenticationFilter;
import com.nguyen.foodrecipe.security.JwtService;
import com.nguyen.foodrecipe.security.UserPrincipal;
import com.nguyen.foodrecipe.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = UserController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class
    )
)
@Import(UserControllerTest.TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        UserService userService() { return mock(UserService.class); }

        @Bean
        JwtService jwtService() { return mock(JwtService.class); }

        @Bean
        CustomUserDetailsService userDetailsService() { return mock(CustomUserDetailsService.class); }
    }

    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
            http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
            return http.build();
        }

        @Bean
        JwtAuthenticationFilter jwtAuthFilter(JwtService js, CustomUserDetailsService uds) {
            return new JwtAuthenticationFilter(js, uds);
        }

        @Bean
        PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    }

    @Test
    void getCurrentUser_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_WithValidToken_ShouldReturnUser() throws Exception {
        User user = User.builder().id(1L).username("testuser").email("test@example.com")
                .password("encoded").role(Role.USER).enabled(true).build();
        UserPrincipal userPrincipal = new UserPrincipal(user);
        UserResponse response = new UserResponse(1L, "testuser", "test@example.com", "USER");

        given(jwtService.extractUsername("valid-token")).willReturn("testuser");
        given(userDetailsService.loadUserByUsername("testuser")).willReturn(userPrincipal);
        given(jwtService.isTokenValid("valid-token", userPrincipal)).willReturn(true);
        given(userService.getCurrentUser(1L)).willReturn(response);

        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    void getCurrentUser_WithInvalidToken_ShouldReturn401() throws Exception {
        given(jwtService.extractUsername("invalid-token")).willThrow(new RuntimeException("Invalid JWT"));
        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_WithExpiredToken_ShouldReturn401() throws Exception {
        given(jwtService.extractUsername("expired-token")).willThrow(new RuntimeException("JWT expired"));
        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer expired-token"))
                .andExpect(status().isUnauthorized());
    }
}
