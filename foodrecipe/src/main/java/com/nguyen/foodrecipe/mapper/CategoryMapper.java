package com.nguyen.foodrecipe.mapper;

import com.nguyen.foodrecipe.dto.CategoryRequest;
import com.nguyen.foodrecipe.dto.CategoryResponse;
import com.nguyen.foodrecipe.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for converting between {@link Category} entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Maps a {@link CategoryRequest} to a new {@link Category} entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipes", ignore = true)
    Category toEntity(CategoryRequest request);

    /**
     * Maps a {@link Category} entity to a {@link CategoryResponse} DTO.
     */
    CategoryResponse toResponse(Category category);

    /**
     * Maps a list of {@link Category} entities to response DTOs.
     */
    List<CategoryResponse> toResponseList(List<Category> categories);

    /**
     * Updates an existing {@link Category} entity from a {@link CategoryRequest}.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipes", ignore = true)
    void updateEntityFromRequest(CategoryRequest request, @MappingTarget Category category);
}
