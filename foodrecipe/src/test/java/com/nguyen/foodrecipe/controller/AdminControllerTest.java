package com.nguyen.foodrecipe.controller;

import com.nguyen.foodrecipe.audit.AdminAuditService;
import com.nguyen.foodrecipe.dto.*;
import com.nguyen.foodrecipe.entity.Role;
import com.nguyen.foodrecipe.entity.User;
import com.nguyen.foodrecipe.security.CustomUserDetailsService;
import com.nguyen.foodrecipe.security.JwtService;
import com.nguyen.foodrecipe.security.UserPrincipal;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminAuditService auditService;

    private static UsernamePasswordAuthenticationToken adminAuth() {
        User user = User.builder().id(1L).username("admin").email("admin@test.com")
                .password("encoded").role(Role.ADMIN).enabled(true).build();
        UserPrincipal principal = new UserPrincipal(user);
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    private static UsernamePasswordAuthenticationToken userAuth() {
        User user = User.builder().id(2L).username("user").email("user@test.com")
                .password("encoded").role(Role.USER).enabled(true).build();
        UserPrincipal principal = new UserPrincipal(user);
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    @TestConfiguration
    @EnableWebSecurity
    static class TestConfig {
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                );
            return http.build();
        }

        @Bean
        RecipeService recipeService() { return mock(RecipeService.class); }

        @Bean
        CategoryService categoryService() { return mock(CategoryService.class); }

        @Bean
        CommentService commentService() { return mock(CommentService.class); }

        @Bean
        AdminService adminService() { return mock(AdminService.class); }

        @Bean
        AdminAuditService auditService() { return mock(AdminAuditService.class); }

        @Bean
        JwtService jwtService() { return mock(JwtService.class); }

        @Bean
        CustomUserDetailsService userDetailsService() { return mock(CustomUserDetailsService.class); }
    }

    // ── Authentication Tests ──

    @Test
    void adminEndpoint_WithoutAuth_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/admin/recipes"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoint_WithUserRole_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/admin/recipes")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(userAuth())))
                .andExpect(status().isForbidden());
    }

    // ── Recipe Management ──

    @Test
    void getAllRecipes_ShouldReturnPaginatedList() throws Exception {
        RecipeSummaryResponse r = new RecipeSummaryResponse(1L, "Test", "img.jpg", "Cat");
        Page<RecipeSummaryResponse> page = new PageImpl<>(List.of(r));
        given(recipeService.getAllRecipes(isNull(), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/admin/recipes")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(adminAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Test"));
    }

    @Test
    void getRecipeById_ShouldReturnDetail() throws Exception {
        RecipeDetailResponse d = new RecipeDetailResponse(1L, "Test", "img.jpg", "Desc",
                null, List.of(), List.of(), 0L, 0.0, 0L, 0L);
        given(recipeService.getRecipeById(1L)).willReturn(d);

        mockMvc.perform(get("/api/admin/recipes/1")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(adminAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Test"));
    }

    @Test
    void createRecipe_ShouldReturn201() throws Exception {
        RecipeResponse resp = new RecipeResponse(1L, "New Recipe", "img.jpg", "Desc",
                LocalDateTime.now(), null, new CategoryResponse(1L, "Cat"),
                Collections.emptyList(), Collections.emptyList());
        given(recipeService.createRecipe(any())).willReturn(resp);
        doNothing().when(auditService).log(anyString(), anyLong(), anyString(), anyString(), anyLong(), anyString());

        mockMvc.perform(post("/api/admin/recipes")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(adminAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"title":"New Recipe","categoryId":1,
                             "ingredients":["flour"],"instructions":[{"stepNumber":1,"instructionText":"Mix"}]}
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("New Recipe"));
    }

    @Test
    void deleteRecipe_ShouldReturn200() throws Exception {
        doNothing().when(recipeService).deleteRecipe(1L);

        mockMvc.perform(delete("/api/admin/recipes/1")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(adminAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── Category Management ──

    @Test
    void createCategory_ShouldReturn201() throws Exception {
        CategoryResponse resp = new CategoryResponse(1L, "Dessert");
        given(categoryService.createCategory(any())).willReturn(resp);

        mockMvc.perform(post("/api/admin/categories")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(adminAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Dessert\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Dessert"));
    }

    @Test
    void deleteCategory_ShouldReturn200() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/admin/categories/1")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(adminAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── User Management ──

    @Test
    void getAllUsers_ShouldReturnPaginatedList() throws Exception {
        AdminUserResponse u = new AdminUserResponse(1L, "admin", "a@t.com", "ADMIN", true, null, null);
        Page<AdminUserResponse> page = new PageImpl<>(List.of(u));
        given(adminService.getAllUsers(isNull(), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/admin/users")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(adminAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].username").value("admin"));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        AdminUserResponse u = new AdminUserResponse(1L, "admin", "a@t.com", "ADMIN", true, LocalDateTime.now(), null);
        given(adminService.getUserById(1L)).willReturn(u);

        mockMvc.perform(get("/api/admin/users/1")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(adminAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    void enableUser_ShouldReturn200() throws Exception {
        doNothing().when(adminService).toggleUserEnabled(anyLong(), eq(true), anyLong(), anyString());

        mockMvc.perform(patch("/api/admin/users/2/enable")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(adminAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void disableUser_ShouldReturn200() throws Exception {
        doNothing().when(adminService).toggleUserEnabled(anyLong(), eq(false), anyLong(), anyString());

        mockMvc.perform(patch("/api/admin/users/2/disable")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(adminAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void changeUserRole_ShouldReturn200() throws Exception {
        doNothing().when(adminService).changeUserRole(anyLong(), anyString(), anyLong(), anyString());

        mockMvc.perform(patch("/api/admin/users/2/role")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(adminAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"ADMIN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── Comment Moderation ──

    @Test
    void getAllComments_ShouldReturnPaginatedList() throws Exception {
        CommentResponse c = new CommentResponse(1L, 1L, 2L, "user", "Great!", LocalDateTime.now(), null);
        Page<CommentResponse> page = new PageImpl<>(List.of(c));
        given(commentService.getAllComments(isNull(), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/admin/comments")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(adminAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].content").value("Great!"));
    }

    @Test
    void deleteComment_ShouldReturn200() throws Exception {
        doNothing().when(commentService).adminDeleteComment(1L);

        mockMvc.perform(delete("/api/admin/comments/1")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(adminAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── Dashboard ──

    @Test
    void getDashboard_ShouldReturnStats() throws Exception {
        DashboardResponse dash = new DashboardResponse(10, 5, 3, 20, 15, 8, 4.5, List.of(), List.of());
        given(adminService.getDashboard()).willReturn(dash);

        mockMvc.perform(get("/api/admin/dashboard")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(adminAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalRecipes").value(10))
                .andExpect(jsonPath("$.data.totalUsers").value(5))
                .andExpect(jsonPath("$.data.averageRating").value(4.5));
    }
}
