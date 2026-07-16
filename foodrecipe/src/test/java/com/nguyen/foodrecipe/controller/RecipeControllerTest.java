package com.nguyen.foodrecipe.controller;

import com.nguyen.foodrecipe.dto.IngredientResponse;
import com.nguyen.foodrecipe.dto.InstructionResponse;
import com.nguyen.foodrecipe.dto.RecipeDetailResponse;
import com.nguyen.foodrecipe.dto.RecipeSummaryResponse;
import com.nguyen.foodrecipe.exception.GlobalExceptionHandler;
import com.nguyen.foodrecipe.exception.RecipeNotFoundException;
import com.nguyen.foodrecipe.security.CustomUserDetailsService;
import com.nguyen.foodrecipe.security.JwtService;
import com.nguyen.foodrecipe.service.CommentService;
import com.nguyen.foodrecipe.service.FavoriteService;
import com.nguyen.foodrecipe.service.RatingService;
import com.nguyen.foodrecipe.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecipeService recipeService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        RecipeService recipeService() {
            return mock(RecipeService.class);
        }

        @Bean
        FavoriteService favoriteService() {
            return mock(FavoriteService.class);
        }

        @Bean
        RatingService ratingService() {
            return mock(RatingService.class);
        }

        @Bean
        CommentService commentService() {
            return mock(CommentService.class);
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
    void getAllRecipes_WithoutKeyword_ShouldReturnPaginatedRecipes() throws Exception {
        RecipeSummaryResponse recipe = new RecipeSummaryResponse(1L, "Test Recipe", "http://example.com/img.jpg", "Dessert");
        Page<RecipeSummaryResponse> page = new PageImpl<>(List.of(recipe));

        given(recipeService.getAllRecipes(any(), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("Test Recipe"))
                .andExpect(jsonPath("$.data.content[0].imageUrl").value("http://example.com/img.jpg"))
                .andExpect(jsonPath("$.data.content[0].categoryName").value("Dessert"));
    }

    @Test
    void getAllRecipes_WithKeyword_ShouldFilterByTitle() throws Exception {
        RecipeSummaryResponse recipe = new RecipeSummaryResponse(2L, "Chicken Soup", "http://example.com/soup.jpg", "Soup");
        Page<RecipeSummaryResponse> page = new PageImpl<>(List.of(recipe));

        given(recipeService.getAllRecipes(eq("chicken"), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/recipes?keyword=chicken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Chicken Soup"));
    }

    @Test
    void getRecipeById_WhenFound_ShouldReturnRecipeDetail() throws Exception {
        RecipeDetailResponse detail = new RecipeDetailResponse(
                1L, "Test Recipe", "http://example.com/img.jpg", "Description",
                null, List.of(), List.of(),
                0L, 0.0, 0L, 0L
        );

        given(recipeService.getRecipeById(1L)).willReturn(detail);

        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Test Recipe"));
    }

    @Test
    void getRecipeById_WhenNotFound_ShouldReturn404() throws Exception {
        given(recipeService.getRecipeById(999L))
                .willThrow(new RecipeNotFoundException(999L));

        mockMvc.perform(get("/api/recipes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Recipe not found with id: 999"));
    }

    @Test
    void searchRecipes_ShouldReturnMatchingResults() throws Exception {
        RecipeSummaryResponse recipe = new RecipeSummaryResponse(3L, "Pasta Carbonara", "http://example.com/pasta.jpg", "Italian");
        Page<RecipeSummaryResponse> page = new PageImpl<>(List.of(recipe));

        given(recipeService.searchRecipes(eq("pasta"), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/recipes/search?keyword=pasta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Pasta Carbonara"));
    }

    @Test
    void searchRecipes_WithoutKeyword_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/recipes/search"))
                .andExpect(status().isBadRequest());
    }
}
