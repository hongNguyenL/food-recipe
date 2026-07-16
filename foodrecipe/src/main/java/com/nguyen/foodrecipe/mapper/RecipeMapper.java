package com.nguyen.foodrecipe.mapper;

import com.nguyen.foodrecipe.dto.IngredientResponse;
import com.nguyen.foodrecipe.dto.InstructionResponse;
import com.nguyen.foodrecipe.dto.RecipeDetailResponse;
import com.nguyen.foodrecipe.dto.RecipeResponse;
import com.nguyen.foodrecipe.dto.RecipeSummaryResponse;
import com.nguyen.foodrecipe.entity.Ingredient;
import com.nguyen.foodrecipe.entity.Instruction;
import com.nguyen.foodrecipe.entity.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface RecipeMapper {

    @Mapping(target = "categoryName", source = "category.name")
    RecipeSummaryResponse toSummaryResponse(Recipe recipe);

    List<RecipeSummaryResponse> toSummaryResponseList(List<Recipe> recipes);

    RecipeDetailResponse toDetailResponse(Recipe recipe);

    RecipeResponse toResponse(Recipe recipe);

    List<RecipeResponse> toResponseList(List<Recipe> recipes);

    IngredientResponse toIngredientResponse(Ingredient ingredient);

    InstructionResponse toInstructionResponse(Instruction instruction);
}
