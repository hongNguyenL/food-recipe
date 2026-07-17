package com.nguyen.foodrecipe.controller;

import com.nguyen.foodrecipe.dto.*;
import com.nguyen.foodrecipe.entity.Role;
import com.nguyen.foodrecipe.entity.User;
import com.nguyen.foodrecipe.exception.CollectionAccessDeniedException;
import com.nguyen.foodrecipe.exception.CollectionNotFoundException;
import com.nguyen.foodrecipe.exception.DuplicateCollectionRecipeException;
import com.nguyen.foodrecipe.exception.UnauthorizedModificationException;
import com.nguyen.foodrecipe.security.CustomUserDetailsService;
import com.nguyen.foodrecipe.security.JwtService;
import com.nguyen.foodrecipe.security.UserPrincipal;
import com.nguyen.foodrecipe.service.CollectionService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CollectionController.class)
class CollectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CollectionService collectionService;

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
        CollectionService collectionService() {
            return mock(CollectionService.class);
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
    void createCollection_ShouldReturn201() throws Exception {
        CollectionResponse response = new CollectionResponse(1L, "Quick Breakfasts",
                "Easy recipes under 20 minutes", "PUBLIC", "testuser", 0,
                LocalDateTime.now(), LocalDateTime.now());

        given(collectionService.createCollection(eq(1L), any(CollectionRequest.class)))
                .willReturn(response);

        mockMvc.perform(post("/api/collections").with(withAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Quick Breakfasts",
                                 "description": "Easy recipes under 20 minutes",
                                 "visibility": "PUBLIC"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Quick Breakfasts"))
                .andExpect(jsonPath("$.data.visibility").value("PUBLIC"));
    }

    @Test
    void updateCollection_ShouldReturn200() throws Exception {
        CollectionResponse response = new CollectionResponse(1L, "Updated Name",
                "Updated description", "PRIVATE", "testuser", 0,
                LocalDateTime.now(), LocalDateTime.now());

        given(collectionService.updateCollection(eq(1L), eq(1L), any(CollectionRequest.class)))
                .willReturn(response);

        mockMvc.perform(put("/api/collections/1").with(withAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Updated Name",
                                 "description": "Updated description",
                                 "visibility": "PRIVATE"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Updated Name"))
                .andExpect(jsonPath("$.data.visibility").value("PRIVATE"));
    }

    @Test
    void deleteCollection_ShouldReturn200() throws Exception {
        doNothing().when(collectionService).deleteCollection(1L, 1L);

        mockMvc.perform(delete("/api/collections/1").with(withAuth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void addRecipeToCollection_ShouldReturn201() throws Exception {
        CollectionRecipeResponse response = new CollectionRecipeResponse(
                1L, 10L, "Pancakes", "http://example.com/pancakes.jpg", LocalDateTime.now());

        given(collectionService.addRecipeToCollection(1L, 1L, 10L)).willReturn(response);

        mockMvc.perform(post("/api/collections/1/recipes/10").with(withAuth()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.recipeId").value(10))
                .andExpect(jsonPath("$.data.recipeTitle").value("Pancakes"));
    }

    @Test
    void removeRecipeFromCollection_ShouldReturn200() throws Exception {
        doNothing().when(collectionService).removeRecipeFromCollection(1L, 1L, 10L);

        mockMvc.perform(delete("/api/collections/1/recipes/10").with(withAuth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void addDuplicateRecipe_ShouldReturn409() throws Exception {
        given(collectionService.addRecipeToCollection(1L, 1L, 10L))
                .willThrow(new DuplicateCollectionRecipeException());

        mockMvc.perform(post("/api/collections/1/recipes/10").with(withAuth()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Recipe already exists in this collection"));
    }

    @Test
    void getPublicCollection_ShouldReturn200() throws Exception {
        CollectionDetailResponse response = new CollectionDetailResponse(
                1L, "Quick Breakfasts", "Easy recipes", "PUBLIC", "testuser",
                LocalDateTime.now(), LocalDateTime.now(), List.of(), 0);

        given(collectionService.getPublicCollectionById(1L)).willReturn(response);

        mockMvc.perform(get("/api/collections/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Quick Breakfasts"))
                .andExpect(jsonPath("$.data.visibility").value("PUBLIC"));
    }

    @Test
    void denyAccessToPrivateCollection_ShouldReturn403() throws Exception {
        given(collectionService.getPublicCollectionById(1L))
                .willThrow(new CollectionAccessDeniedException("This collection is not public"));

        mockMvc.perform(get("/api/collections/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("This collection is not public"));
    }

    @Test
    void getMyCollections_ShouldReturnPaginatedResults() throws Exception {
        CollectionSummaryResponse c1 = new CollectionSummaryResponse(
                1L, "Breakfast", "Quick meals", "PUBLIC", "testuser", 3, LocalDateTime.now());
        CollectionSummaryResponse c2 = new CollectionSummaryResponse(
                2L, "Desserts", "Sweet treats", "PRIVATE", "testuser", 5, LocalDateTime.now());
        Page<CollectionSummaryResponse> page = new PageImpl<>(List.of(c1, c2));

        given(collectionService.getUserCollections(eq(1L), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/users/me/collections").with(withAuth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].name").value("Breakfast"))
                .andExpect(jsonPath("$.data.content[1].name").value("Desserts"));
    }

    @Test
    void getPublicCollections_ShouldReturnPublicOnly() throws Exception {
        CollectionSummaryResponse c1 = new CollectionSummaryResponse(
                1L, "Breakfast", "Quick meals", "PUBLIC", "chef1", 3, LocalDateTime.now());
        Page<CollectionSummaryResponse> page = new PageImpl<>(List.of(c1));

        given(collectionService.getPublicCollections(any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/collections/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].name").value("Breakfast"))
                .andExpect(jsonPath("$.data.content[0].visibility").value("PUBLIC"));
    }

    @Test
    void searchCollections_ByKeyword_ShouldReturnFilteredResults() throws Exception {
        CollectionSummaryResponse c1 = new CollectionSummaryResponse(
                1L, "Healthy Breakfast", "Nutritious meals", "PUBLIC", "chef1", 5, LocalDateTime.now());
        Page<CollectionSummaryResponse> page = new PageImpl<>(List.of(c1));

        given(collectionService.searchCollections(eq("breakfast"), isNull(), isNull(), any(Pageable.class)))
                .willReturn(page);

        mockMvc.perform(get("/api/collections/search?keyword=breakfast"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].name").value("Healthy Breakfast"));
    }

    @Test
    void searchCollections_ByOwnerAndVisibility_ShouldFilter() throws Exception {
        CollectionSummaryResponse c1 = new CollectionSummaryResponse(
                1L, "Quick Meals", "Fast recipes", "PUBLIC", "chef2", 2, LocalDateTime.now());
        Page<CollectionSummaryResponse> page = new PageImpl<>(List.of(c1));

        given(collectionService.searchCollections(isNull(), eq("chef2"), eq("PUBLIC"), any(Pageable.class)))
                .willReturn(page);

        mockMvc.perform(get("/api/collections/search?ownerUsername=chef2&visibility=PUBLIC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].ownerUsername").value("chef2"));
    }

    @Test
    void getCollection_WhenNotFound_ShouldReturn404() throws Exception {
        given(collectionService.getPublicCollectionById(999L))
                .willThrow(new CollectionNotFoundException(999L));

        mockMvc.perform(get("/api/collections/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Collection not found with id: 999"));
    }

    @Test
    void createCollection_WithoutName_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/collections").with(withAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "",
                                 "description": "Some desc",
                                 "visibility": "PUBLIC"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getCollectionRecipes_ShouldReturnPaginated() throws Exception {
        CollectionRecipeResponse r1 = new CollectionRecipeResponse(
                1L, 10L, "Pancakes", "http://example.com/pancakes.jpg", LocalDateTime.now());
        Page<CollectionRecipeResponse> page = new PageImpl<>(List.of(r1));

        given(collectionService.getPublicCollectionRecipes(eq(1L), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/collections/1/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].recipeTitle").value("Pancakes"));
    }
}
