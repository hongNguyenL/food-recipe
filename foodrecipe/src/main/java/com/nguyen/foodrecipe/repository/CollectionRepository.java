package com.nguyen.foodrecipe.repository;

import com.nguyen.foodrecipe.entity.CollectionVisibility;
import com.nguyen.foodrecipe.entity.RecipeCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<RecipeCollection, Long> {

    @EntityGraph(attributePaths = {"owner"})
    Page<RecipeCollection> findByOwnerId(Long ownerId, Pageable pageable);

    @EntityGraph(attributePaths = {"owner"})
    Page<RecipeCollection> findByVisibility(CollectionVisibility visibility, Pageable pageable);

    @EntityGraph(attributePaths = {"owner"})
    Optional<RecipeCollection> findById(Long id);

    @EntityGraph(attributePaths = {"owner"})
    @Query("SELECT c FROM RecipeCollection c WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:ownerUsername IS NULL OR :ownerUsername = '' OR LOWER(c.owner.username) LIKE LOWER(CONCAT('%', :ownerUsername, '%'))) AND " +
            "(:visibility IS NULL OR c.visibility = :visibility)")
    Page<RecipeCollection> searchCollections(
            @Param("keyword") String keyword,
            @Param("ownerUsername") String ownerUsername,
            @Param("visibility") CollectionVisibility visibility,
            Pageable pageable);
}
