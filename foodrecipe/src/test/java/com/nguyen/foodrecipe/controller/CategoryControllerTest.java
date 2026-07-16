package com.nguyen.foodrecipe.controller;

import com.nguyen.foodrecipe.dto.CategoryResponse;
import com.nguyen.foodrecipe.dto.RecipeSummaryResponse;
import com.nguyen.foodrecipe.service.CategoryService;
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

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RecipeService recipeService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        CategoryService categoryService() {
            return mock(CategoryService.class);
        }

        @Bean
        RecipeService recipeService() {
            return mock(RecipeService.class);
        }
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() throws Exception {
        List<CategoryResponse> categories = List.of(
                new CategoryResponse(1L, "Dessert"),
                new CategoryResponse(2L, "Soup")
        );

        given(categoryService.getAllCategories()).willReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Dessert"))
                .andExpect(jsonPath("$.data[1].name").value("Soup"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getRecipesByCategory_ShouldReturnPaginatedRecipes() throws Exception {
        RecipeSummaryResponse recipe = new RecipeSummaryResponse(1L, "Chocolate Cake", "http://example.com/cake.jpg", "Dessert");
        Page<RecipeSummaryResponse> page = new PageImpl<>(List.of(recipe));

        given(recipeService.getRecipesByCategory(eq(1L), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/categories/1/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].categoryName").value("Dessert"));
    }
}
