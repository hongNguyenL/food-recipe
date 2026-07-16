package com.nguyen.foodrecipe.mapper;

import com.nguyen.foodrecipe.dto.RatingResponse;
import com.nguyen.foodrecipe.entity.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mapping(target = "recipeId", source = "recipe.id")
    RatingResponse toResponse(Rating rating);
}
