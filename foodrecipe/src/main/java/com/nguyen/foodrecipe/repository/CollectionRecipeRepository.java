package com.nguyen.foodrecipe.repository;

import com.nguyen.foodrecipe.entity.CollectionRecipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionRecipeRepository extends JpaRepository<CollectionRecipe, Long> {

    @EntityGraph(attributePaths = {"recipe"})
    Page<CollectionRecipe> findByCollectionId(Long collectionId, Pageable pageable);

    Optional<CollectionRecipe> findByCollectionIdAndRecipeId(Long collectionId, Long recipeId);

    boolean existsByCollectionIdAndRecipeId(Long collectionId, Long recipeId);

    long countByCollectionId(Long collectionId);

    void deleteByCollectionIdAndRecipeId(Long collectionId, Long recipeId);
}
