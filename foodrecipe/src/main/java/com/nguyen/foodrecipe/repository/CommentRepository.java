package com.nguyen.foodrecipe.repository;

import com.nguyen.foodrecipe.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<Comment> findByRecipeIdOrderByCreatedAtDesc(Long recipeId, Pageable pageable);

    long countByRecipeId(Long recipeId);
}
