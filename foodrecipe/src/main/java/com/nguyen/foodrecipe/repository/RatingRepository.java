package com.nguyen.foodrecipe.repository;

import com.nguyen.foodrecipe.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByUserIdAndRecipeId(Long userId, Long recipeId);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.recipe.id = :recipeId")
    Double findAverageRatingByRecipeId(@Param("recipeId") Long recipeId);

    long countByRecipeId(Long recipeId);

    @Query("SELECT r.rating FROM Rating r WHERE r.user.id = :userId AND r.recipe.id = :recipeId")
    Optional<Integer> findRatingByUserIdAndRecipeId(@Param("userId") Long userId, @Param("recipeId") Long recipeId);

    @Query("SELECT AVG(r.rating) FROM Rating r")
    Optional<Double> findGlobalAverageRating();
}
