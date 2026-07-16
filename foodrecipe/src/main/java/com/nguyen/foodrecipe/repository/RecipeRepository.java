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
}
