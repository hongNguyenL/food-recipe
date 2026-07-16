package com.nguyen.foodrecipe.service.impl;

import com.nguyen.foodrecipe.dto.RatingResponse;
import com.nguyen.foodrecipe.dto.RecipeRatingResponse;
import com.nguyen.foodrecipe.entity.Rating;
import com.nguyen.foodrecipe.entity.Recipe;
import com.nguyen.foodrecipe.entity.User;
import com.nguyen.foodrecipe.exception.RecipeNotFoundException;
import com.nguyen.foodrecipe.mapper.RatingMapper;
import com.nguyen.foodrecipe.repository.RatingRepository;
import com.nguyen.foodrecipe.repository.RecipeRepository;
import com.nguyen.foodrecipe.repository.UserRepository;
import com.nguyen.foodrecipe.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RatingMapper ratingMapper;

    @Override
    @Transactional
    public RatingResponse rateRecipe(Long userId, Long recipeId, int rating) {
        log.debug("Rating recipe: user={}, recipe={}, rating={}", userId, recipeId, rating);
        if (!recipeRepository.existsById(recipeId)) {
            throw new RecipeNotFoundException(recipeId);
        }
        Optional<Rating> existing = ratingRepository.findByUserIdAndRecipeId(userId, recipeId);
        if (existing.isPresent()) {
            Rating r = existing.get();
            r.setRating(rating);
            Rating saved = ratingRepository.save(r);
            log.info("Rating updated: user={}, recipe={}, rating={}", userId, recipeId, rating);
            return ratingMapper.toResponse(saved);
        }
        User user = userRepository.getReferenceById(userId);
        Recipe recipe = recipeRepository.getReferenceById(recipeId);
        Rating newRating = Rating.builder().user(user).recipe(recipe).rating(rating).build();
        Rating saved = ratingRepository.save(newRating);
        log.info("Rating created: user={}, recipe={}, rating={}", userId, recipeId, rating);
        return ratingMapper.toResponse(saved);
    }

    @Override
    public RecipeRatingResponse getRecipeRating(Long recipeId, Long currentUserId) {
        log.debug("Fetching rating stats for recipe: {}", recipeId);
        if (!recipeRepository.existsById(recipeId)) {
            throw new RecipeNotFoundException(recipeId);
        }
        Double avg = ratingRepository.findAverageRatingByRecipeId(recipeId);
        long total = ratingRepository.countByRecipeId(recipeId);
        double averageRating = (avg != null) ? Math.round(avg * 10.0) / 10.0 : 0.0;
        Integer currentUserRating = null;
        if (currentUserId != null) {
            currentUserRating = ratingRepository.findRatingByUserIdAndRecipeId(currentUserId, recipeId)
                    .orElse(null);
        }
        return new RecipeRatingResponse(averageRating, total, currentUserRating);
    }
}
