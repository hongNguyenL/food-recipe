package com.nguyen.foodrecipe.repository;

import com.nguyen.foodrecipe.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<Comment> findByRecipeIdOrderByCreatedAtDesc(Long recipeId, Pageable pageable);

    long countByRecipeId(Long recipeId);

    @EntityGraph(attributePaths = {"user", "recipe"})
    @Query("SELECT c FROM Comment c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.user.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.recipe.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Comment> searchComments(@Param("keyword") String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "recipe"})
    Page<Comment> findAll(Pageable pageable);
}
