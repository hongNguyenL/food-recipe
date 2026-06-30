package com.nguyen.foodrecipe.repository;

import com.nguyen.foodrecipe.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Recipe} entities.
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * Finds a single recipe by ID and eagerly fetches its associations
     * to avoid N+1 queries on the detail endpoint.
     *
     * @param id the recipe ID
     * @return an optional containing the recipe with all associations loaded
     */
    @EntityGraph(attributePaths = {"ingredients", "instructions", "category"})
    Optional<Recipe> findWithDetailsById(Long id);

    /**
     * Searches recipes whose title matches the keyword (case-insensitive)
     * using PostgreSQL {@code ILIKE}.
     *
     * @param keyword  the search term
     * @param pageable pagination/sorting parameters
     * @return a page of matching recipes
     */
    @Query(value = "SELECT * FROM recipes WHERE title ILIKE CONCAT('%', :keyword, '%')",
            countQuery = "SELECT COUNT(*) FROM recipes WHERE title ILIKE CONCAT('%', :keyword, '%')",
            nativeQuery = true)
    Page<Recipe> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Finds all recipes belonging to a given category.
     *
     * @param categoryId the category ID to filter by
     * @param pageable   pagination/sorting parameters
     * @return a page of recipes in the specified category
     */
    Page<Recipe> findByCategoryId(Long categoryId, Pageable pageable);
}
