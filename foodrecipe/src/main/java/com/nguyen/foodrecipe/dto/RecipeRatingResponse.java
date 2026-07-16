package com.nguyen.foodrecipe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RecipeRatingResponse(
        double averageRating,
        long totalRatings,
        Integer currentUserRating
) {}
