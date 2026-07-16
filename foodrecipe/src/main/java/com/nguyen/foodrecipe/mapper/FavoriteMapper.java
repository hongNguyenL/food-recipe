package com.nguyen.foodrecipe.mapper;

import com.nguyen.foodrecipe.dto.FavoriteResponse;
import com.nguyen.foodrecipe.entity.Favorite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

    @Mapping(target = "recipeId", source = "recipe.id")
    FavoriteResponse toResponse(Favorite favorite);
}
