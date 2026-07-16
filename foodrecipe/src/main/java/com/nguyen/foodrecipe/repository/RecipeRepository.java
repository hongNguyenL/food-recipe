package com.nguyen.foodrecipe.repository;

import com.nguyen.foodrecipe.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Recommended PostgreSQL indexes for production (50K+ recipes):
 * <pre>
 * CREATE INDEX idx_recipes_title ON recipes USING gin (title gin_trgm_ops);
 * CREATE INDEX idx_recipes_category_id ON recipes (category_id);
 * CREATE INDEX idx_recipes_created_at ON recipes (created_at);
 * CREATE INDEX idx_ingredients_text ON ingredients USING gin (ingredient_text gin_trgm_ops);
 * CREATE INDEX idx_ingredients_recipe_id ON ingredients (recipe_id);
 * CREATE INDEX idx_favorites_recipe_id ON favorites (recipe_id);
 * CREATE INDEX idx_favorites_user_id ON favorites (user_id);
 * CREATE INDEX idx_ratings_recipe_id ON ratings (recipe_id);
 * CREATE INDEX idx_comments_recipe_id ON comments (recipe_id);
 * </pre>
 * The trigram indexes (gin_trgm_ops) are required for efficient {@code LIKE '%keyword%'}
 * searches on title and ingredient_text. Enable via: {@code CREATE EXTENSION IF NOT EXISTS pg_trgm;}
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @EntityGraph(attributePaths = {"category"})
    Page<Recipe> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"ingredients", "instructions", "category"})
    Optional<Recipe> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Recipe> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<Recipe> findByCategoryId(Long categoryId, Pageable pageable);

    @Query(nativeQuery = true,
           value = "SELECT r.id, r.title, r.image_url, c.name, r.created_at, " +
                   "COALESCE((SELECT AVG(rt.rating) FROM ratings rt WHERE rt.recipe_id = r.id), 0) as avg_rating, " +
                   "COALESCE((SELECT COUNT(*) FROM favorites f WHERE f.recipe_id = r.id), 0) as fav_count, " +
                   "COALESCE((SELECT COUNT(*) FROM comments com WHERE com.recipe_id = r.id), 0) as com_count " +
                   "FROM recipes r " +
                   "JOIN categories c ON c.id = r.category_id " +
                   "WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                   "AND (:categoryId IS NULL OR r.category_id = :categoryId) " +
                   "AND (:ingredient IS NULL OR :ingredient = '' OR r.id IN (SELECT i.recipe_id FROM ingredients i WHERE LOWER(i.ingredient_text) LIKE LOWER(CONCAT('%', :ingredient, '%'))))",
           countQuery = "SELECT COUNT(*) FROM recipes r " +
                        "WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                        "AND (:categoryId IS NULL OR r.category_id = :categoryId) " +
                        "AND (:ingredient IS NULL OR :ingredient = '' OR r.id IN (SELECT i.recipe_id FROM ingredients i WHERE LOWER(i.ingredient_text) LIKE LOWER(CONCAT('%', :ingredient, '%'))))")
    Page<Object[]> searchRecipesNative(@Param("keyword") String keyword,
                                        @Param("categoryId") Long categoryId,
                                        @Param("ingredient") String ingredient,
                                        Pageable pageable);

    @Query(nativeQuery = true,
           value = "SELECT r.id, r.title, r.image_url, c.name, " +
                   "COALESCE(rs.avg_rating, 0) as avg_rating, " +
                   "COALESCE(fs.cnt, 0) as fav_count, " +
                   "COALESCE(cs.cnt, 0) as com_count, " +
                   "(COALESCE(rs.avg_rating, 0) * 3 + COALESCE(fs.cnt, 0) * 2 + COALESCE(cs.cnt, 0) * 1) as popularity_score " +
                   "FROM recipes r " +
                   "JOIN categories c ON c.id = r.category_id " +
                   "LEFT JOIN (SELECT recipe_id, AVG(rating) as avg_rating FROM ratings GROUP BY recipe_id) rs ON rs.recipe_id = r.id " +
                   "LEFT JOIN (SELECT recipe_id, COUNT(*) as cnt FROM favorites GROUP BY recipe_id) fs ON fs.recipe_id = r.id " +
                   "LEFT JOIN (SELECT recipe_id, COUNT(*) as cnt FROM comments GROUP BY recipe_id) cs ON cs.recipe_id = r.id " +
                   "ORDER BY popularity_score DESC")
    Page<Object[]> findPopularRecipes(Pageable pageable);

    @Query(nativeQuery = true,
           value = "SELECT r.id, r.title, r.image_url, c.name, " +
                   "COALESCE(AVG(rt.rating), 0) as avg_rating " +
                   "FROM recipes r " +
                   "JOIN categories c ON c.id = r.category_id " +
                   "LEFT JOIN ratings rt ON rt.recipe_id = r.id " +
                   "GROUP BY r.id, c.name, r.title, r.image_url " +
                   "ORDER BY avg_rating DESC")
    Page<Object[]> findTopRatedRecipes(Pageable pageable);

    @Query(nativeQuery = true,
           value = "SELECT r.id, r.title, r.image_url, c.name, " +
                   "COALESCE(rs.avg_rating, 0) as avg_rating, " +
                   "CASE WHEN r.category_id = :categoryId THEN 2 ELSE 0 END + COALESCE(ic.shared_count, 0) as relevance_score " +
                   "FROM recipes r " +
                   "JOIN categories c ON c.id = r.category_id " +
                   "LEFT JOIN (SELECT recipe_id, AVG(rating) as avg_rating FROM ratings GROUP BY recipe_id) rs ON rs.recipe_id = r.id " +
                   "LEFT JOIN (SELECT i2.recipe_id, COUNT(DISTINCT i2.ingredient_text) as shared_count " +
                   "           FROM ingredients i2 " +
                   "           WHERE i2.ingredient_text IN (SELECT i1.ingredient_text FROM ingredients i1 WHERE i1.recipe_id = :recipeId) " +
                   "           AND i2.recipe_id != :recipeId " +
                   "           GROUP BY i2.recipe_id) ic ON ic.recipe_id = r.id " +
                   "WHERE r.id != :recipeId " +
                   "AND (r.category_id = :categoryId OR ic.shared_count > 0) " +
                   "ORDER BY relevance_score DESC, avg_rating DESC")
    List<Object[]> findSimilarRecipes(@Param("recipeId") Long recipeId,
                                      @Param("categoryId") Long categoryId,
                                      Pageable pageable);
}
