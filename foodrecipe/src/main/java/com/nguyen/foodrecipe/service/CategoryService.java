package com.nguyen.foodrecipe.service;

import com.nguyen.foodrecipe.dto.CategoryRequest;
import com.nguyen.foodrecipe.dto.CategoryResponse;

import java.util.List;

/**
 * Service interface for {@link com.nguyen.foodrecipe.entity.Category} operations.
 */
public interface CategoryService {

    /**
     * Creates a new category.
     *
     * @param request the category data
     * @return the created category
     */
    CategoryResponse createCategory(CategoryRequest request);

    /**
     * Updates an existing category.
     *
     * @param id      the category ID
     * @param request the updated category data
     * @return the updated category
     */
    CategoryResponse updateCategory(Long id, CategoryRequest request);

    /**
     * Deletes a category by ID.
     *
     * @param id the category ID
     */
    void deleteCategory(Long id);

    /**
     * Returns all categories.
     *
     * @return list of all categories
     */
    List<CategoryResponse> getAllCategories();

    /**
     * Returns a single category by ID.
     *
     * @param id the category ID
     * @return the matching category
     */
    CategoryResponse getCategoryById(Long id);
}
