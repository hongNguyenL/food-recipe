package com.nguyen.foodrecipe.controller;

import com.nguyen.foodrecipe.dto.CommentRequest;
import com.nguyen.foodrecipe.dto.CommentResponse;
import com.nguyen.foodrecipe.entity.Role;
import com.nguyen.foodrecipe.entity.User;
import com.nguyen.foodrecipe.security.CustomUserDetailsService;
import com.nguyen.foodrecipe.security.JwtService;
import com.nguyen.foodrecipe.security.UserPrincipal;
import com.nguyen.foodrecipe.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentService commentService;

    @TestConfiguration
    @EnableWebSecurity
    static class TestConfig {
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

        @Bean
        CommentService commentService() { return mock(CommentService.class); }

        @Bean
        JwtService jwtService() { return mock(JwtService.class); }

        @Bean
        CustomUserDetailsService userDetailsService() { return mock(CustomUserDetailsService.class); }
    }

    private static UsernamePasswordAuthenticationToken auth() {
        User user = User.builder().id(1L).username("testuser").email("test@example.com")
                .password("encoded").role(Role.USER).enabled(true).build();
        UserPrincipal principal = new UserPrincipal(user);
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    @Test
    void editOwnComment_ShouldReturn200() throws Exception {
        CommentResponse response = new CommentResponse(1L, 1L, 1L, "testuser",
                "Updated comment!", null, null);

        given(commentService.updateComment(eq(1L), eq(1L), any(CommentRequest.class)))
                .willReturn(response);

        mockMvc.perform(put("/api/comments/1")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(auth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Updated comment!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value("Updated comment!"));
    }

    @Test
    void deleteOwnComment_ShouldReturn200() throws Exception {
        doNothing().when(commentService).deleteComment(1L, 1L, "USER");

        mockMvc.perform(delete("/api/comments/1")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
