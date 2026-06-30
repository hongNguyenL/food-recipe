package com.nguyen.foodrecipe.mapper;

import com.nguyen.foodrecipe.dto.IngredientResponse;
import com.nguyen.foodrecipe.dto.InstructionResponse;
import com.nguyen.foodrecipe.dto.RecipeResponse;
import com.nguyen.foodrecipe.entity.Ingredient;
import com.nguyen.foodrecipe.entity.Instruction;
import com.nguyen.foodrecipe.entity.Recipe;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper for converting {@link Recipe} entities and their children to DTOs.
 * <p>
 * Request-to-entity mapping is handled manually in the service layer because
 * it involves looking up the {@code Category} and building bidirectional
 * relationships for ingredients and instructions.
 * </p>
 */
@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface RecipeMapper {

    /**
     * Maps a {@link Recipe} entity (with eager-loaded associations) to a full response DTO.
     */
    RecipeResponse toResponse(Recipe recipe);

    /**
     * Maps a list of {@link Recipe} entities to response DTOs.
     */
    List<RecipeResponse> toResponseList(List<Recipe> recipes);

    /**
     * Maps an {@link Ingredient} entity to its response DTO.
     */
    IngredientResponse toIngredientResponse(Ingredient ingredient);

    /**
     * Maps an {@link Instruction} entity to its response DTO.
     */
    InstructionResponse toInstructionResponse(Instruction instruction);
}
