package com.nguyen.foodrecipe.controller;

import com.nguyen.foodrecipe.dto.*;
import com.nguyen.foodrecipe.entity.Role;
import com.nguyen.foodrecipe.entity.User;
import com.nguyen.foodrecipe.security.UserPrincipal;
import com.nguyen.foodrecipe.security.CustomUserDetailsService;
import com.nguyen.foodrecipe.security.JwtService;
import com.nguyen.foodrecipe.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RecipeController.class)
class RecipeInteractionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private CommentService commentService;

    private static UsernamePasswordAuthenticationToken auth() {
        User user = User.builder().id(1L).username("testuser").email("test@example.com")
                .password("encoded").role(Role.USER).enabled(true).build();
        UserPrincipal principal = new UserPrincipal(user);
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    private static org.springframework.test.web.servlet.request.RequestPostProcessor withAuth() {
        return SecurityMockMvcRequestPostProcessors.authentication(auth());
    }

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
        RecipeService recipeService() { return mock(RecipeService.class); }

        @Bean
        FavoriteService favoriteService() { return mock(FavoriteService.class); }

        @Bean
        RatingService ratingService() { return mock(RatingService.class); }

        @Bean
        CommentService commentService() { return mock(CommentService.class); }

        @Bean
        JwtService jwtService() { return mock(JwtService.class); }

        @Bean
        CustomUserDetailsService userDetailsService() { return mock(CustomUserDetailsService.class); }
    }

    @Test
    void addFavorite_ShouldReturn201() throws Exception {
        doNothing().when(favoriteService).addFavorite(1L, 1L);

        mockMvc.perform(post("/api/recipes/1/favorite").with(withAuth()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void removeFavorite_ShouldReturn200() throws Exception {
        doNothing().when(favoriteService).removeFavorite(1L, 1L);

        mockMvc.perform(delete("/api/recipes/1/favorite").with(withAuth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void rateRecipe_ShouldReturn200() throws Exception {
        RatingResponse response = new RatingResponse(1L, 1L, 4);
        given(ratingService.rateRecipe(1L, 1L, 4)).willReturn(response);

        mockMvc.perform(post("/api/recipes/1/rating").with(withAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rating").value(4));
    }

    @Test
    void updateRating_ShouldReturn200() throws Exception {
        RatingResponse response = new RatingResponse(1L, 1L, 5);
        given(ratingService.rateRecipe(1L, 1L, 5)).willReturn(response);

        mockMvc.perform(post("/api/recipes/1/rating").with(withAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rating").value(5));
    }

    @Test
    void addComment_ShouldReturn201() throws Exception {
        CommentResponse response = new CommentResponse(1L, 1L, 1L, "testuser",
                "Great recipe!", null, null);
        given(commentService.createComment(eq(1L), eq(1L), any(CommentRequest.class)))
                .willReturn(response);

        mockMvc.perform(post("/api/recipes/1/comments").with(withAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Great recipe!\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value("Great recipe!"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    void getRecipeComments_ShouldReturn200() throws Exception {
        CommentResponse comment = new CommentResponse(1L, 1L, 1L, "testuser",
                "Nice!", null, null);
        Page<CommentResponse> page = new PageImpl<>(List.of(comment));
        given(commentService.getRecipeComments(eq(1L), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/recipes/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].content").value("Nice!"));
    }
}
