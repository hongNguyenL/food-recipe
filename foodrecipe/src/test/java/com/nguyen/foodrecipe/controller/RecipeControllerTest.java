package com.nguyen.foodrecipe.controller;

import com.nguyen.foodrecipe.dto.IngredientResponse;
import com.nguyen.foodrecipe.dto.InstructionResponse;
import com.nguyen.foodrecipe.dto.PopularRecipeResponse;
import com.nguyen.foodrecipe.dto.RecipeDetailResponse;
import com.nguyen.foodrecipe.dto.RecipeSummaryResponse;
import com.nguyen.foodrecipe.dto.SearchRecipeResponse;
import com.nguyen.foodrecipe.dto.SimilarRecipeResponse;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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
    void searchRecipes_ByKeyword_ShouldReturnMatchingResults() throws Exception {
        SearchRecipeResponse recipe = new SearchRecipeResponse(
                3L, "Pasta Carbonara", "http://example.com/pasta.jpg", "Italian",
                LocalDateTime.now(), 4.5, 10, 5);
        Page<SearchRecipeResponse> page = new PageImpl<>(List.of(recipe));

        given(recipeService.advancedSearch(eq("pasta"), isNull(), isNull(), any(Pageable.class)))
                .willReturn(page);

        mockMvc.perform(get("/api/recipes/search?keyword=pasta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Pasta Carbonara"))
                .andExpect(jsonPath("$.data.content[0].averageRating").value(4.5));
    }

    @Test
    void searchRecipes_ByKeywordAndCategory_ShouldFilterByBoth() throws Exception {
        SearchRecipeResponse recipe = new SearchRecipeResponse(
                4L, "Chicken Curry", "http://example.com/curry.jpg", "Curry",
                LocalDateTime.now(), 4.2, 15, 8);
        Page<SearchRecipeResponse> page = new PageImpl<>(List.of(recipe));

        given(recipeService.advancedSearch(eq("chicken"), eq(2L), isNull(), any(Pageable.class)))
                .willReturn(page);

        mockMvc.perform(get("/api/recipes/search?keyword=chicken&categoryId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Chicken Curry"));
    }

    @Test
    void searchRecipes_ByIngredient_ShouldReturnMatchingResults() throws Exception {
        SearchRecipeResponse recipe = new SearchRecipeResponse(
                5L, "Garlic Bread", "http://example.com/bread.jpg", "Appetizer",
                LocalDateTime.now(), 4.0, 20, 3);
        Page<SearchRecipeResponse> page = new PageImpl<>(List.of(recipe));

        given(recipeService.advancedSearch(isNull(), isNull(), eq("garlic"), any(Pageable.class)))
                .willReturn(page);

        mockMvc.perform(get("/api/recipes/search?ingredient=garlic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Garlic Bread"));
    }

    @Test
    void searchRecipes_WithoutFilters_ShouldReturnAll() throws Exception {
        SearchRecipeResponse r1 = new SearchRecipeResponse(
                1L, "Recipe A", "http://example.com/a.jpg", "Cat1",
                LocalDateTime.now(), 3.0, 5, 1);
        SearchRecipeResponse r2 = new SearchRecipeResponse(
                2L, "Recipe B", "http://example.com/b.jpg", "Cat2",
                LocalDateTime.now(), 4.0, 8, 2);
        Page<SearchRecipeResponse> page = new PageImpl<>(List.of(r1, r2));

        given(recipeService.advancedSearch(isNull(), isNull(), isNull(), any(Pageable.class)))
                .willReturn(page);

        mockMvc.perform(get("/api/recipes/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    void searchRecipes_CombinedFilters_ShouldReturnFilteredResults() throws Exception {
        SearchRecipeResponse recipe = new SearchRecipeResponse(
                6L, "Chicken Alfredo", "http://example.com/alfredo.jpg", "Italian",
                LocalDateTime.now(), 4.8, 30, 12);
        Page<SearchRecipeResponse> page = new PageImpl<>(List.of(recipe));

        given(recipeService.advancedSearch(eq("chicken"), eq(1L), eq("garlic"), any(Pageable.class)))
                .willReturn(page);

        mockMvc.perform(get("/api/recipes/search?keyword=chicken&categoryId=1&ingredient=garlic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Chicken Alfredo"));
    }

    @Test
    void getPopularRecipes_ShouldReturnWeightedResults() throws Exception {
        PopularRecipeResponse recipe = new PopularRecipeResponse(
                1L, "Popular Dish", "http://example.com/pop.jpg", "Main",
                4.5, 100, 50, 4.5 * 3 + 100 * 2 + 50);
        Page<PopularRecipeResponse> page = new PageImpl<>(List.of(recipe));

        given(recipeService.getPopularRecipes(any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/recipes/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Popular Dish"))
                .andExpect(jsonPath("$.data.content[0].popularityScore").value(263.5));
    }

    @Test
    void getTopRatedRecipes_ShouldReturnByRatingDesc() throws Exception {
        SearchRecipeResponse recipe = new SearchRecipeResponse(
                1L, "Top Dish", "http://example.com/top.jpg", "Main",
                LocalDateTime.now(), 5.0, 50, 20);
        Page<SearchRecipeResponse> page = new PageImpl<>(List.of(recipe));

        given(recipeService.getTopRatedRecipes(any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/recipes/top-rated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Top Dish"))
                .andExpect(jsonPath("$.data.content[0].averageRating").value(5.0));
    }

    @Test
    void getLatestRecipes_ShouldReturnNewestFirst() throws Exception {
        RecipeSummaryResponse recipe = new RecipeSummaryResponse(
                1L, "New Recipe", "http://example.com/new.jpg", "Dessert");
        Page<RecipeSummaryResponse> page = new PageImpl<>(List.of(recipe));

        given(recipeService.getLatestRecipes(any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/recipes/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("New Recipe"));
    }

    @Test
    void getSimilarRecipes_ShouldReturnRecommendedRecipes() throws Exception {
        SimilarRecipeResponse r1 = new SimilarRecipeResponse(
                2L, "Similar Dish", "http://example.com/sim.jpg", "Italian", 4.3);
        SimilarRecipeResponse r2 = new SimilarRecipeResponse(
                3L, "Related Dish", "http://example.com/rel.jpg", "Italian", 4.1);
        List<SimilarRecipeResponse> list = List.of(r1, r2);

        given(recipeService.getSimilarRecipes(1L)).willReturn(list);

        mockMvc.perform(get("/api/recipes/1/similar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].title").value("Similar Dish"))
                .andExpect(jsonPath("$.data[1].title").value("Related Dish"));
    }
}
