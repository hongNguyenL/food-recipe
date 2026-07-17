package com.nguyen.foodrecipe.mapper;

import com.nguyen.foodrecipe.dto.*;
import com.nguyen.foodrecipe.entity.CollectionRecipe;
import com.nguyen.foodrecipe.entity.RecipeCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CollectionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "collectionRecipes", ignore = true)
    @Mapping(target = "visibility", expression = "java(com.nguyen.foodrecipe.entity.CollectionVisibility.valueOf(request.visibility()))")
    RecipeCollection toEntity(CollectionRequest request);

    @Mapping(target = "ownerUsername", source = "owner.username")
    @Mapping(target = "recipeCount", expression = "java(collection.getCollectionRecipes() != null ? collection.getCollectionRecipes().size() : 0)")
    @Mapping(target = "visibility", expression = "java(collection.getVisibility().name())")
    CollectionResponse toResponse(RecipeCollection collection);

    List<CollectionResponse> toResponseList(List<RecipeCollection> collections);

    @Mapping(target = "ownerUsername", source = "owner.username")
    @Mapping(target = "recipeCount", expression = "java(collection.getCollectionRecipes() != null ? collection.getCollectionRecipes().size() : 0)")
    @Mapping(target = "visibility", expression = "java(collection.getVisibility().name())")
    CollectionSummaryResponse toSummaryResponse(RecipeCollection collection);

    List<CollectionSummaryResponse> toSummaryResponseList(List<RecipeCollection> collections);

    @Mapping(target = "recipeId", source = "recipe.id")
    @Mapping(target = "recipeTitle", source = "recipe.title")
    @Mapping(target = "recipeImageUrl", source = "recipe.imageUrl")
    CollectionRecipeResponse toRecipeResponse(CollectionRecipe collectionRecipe);

    List<CollectionRecipeResponse> toRecipeResponseList(List<CollectionRecipe> collectionRecipes);
}
