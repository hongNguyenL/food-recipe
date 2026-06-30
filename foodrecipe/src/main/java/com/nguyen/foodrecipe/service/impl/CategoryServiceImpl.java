package com.nguyen.foodrecipe.service.impl;

import com.nguyen.foodrecipe.dto.CategoryRequest;
import com.nguyen.foodrecipe.dto.CategoryResponse;
import com.nguyen.foodrecipe.entity.Category;
import com.nguyen.foodrecipe.exception.CategoryNotFoundException;
import com.nguyen.foodrecipe.exception.DuplicateCategoryException;
import com.nguyen.foodrecipe.mapper.CategoryMapper;
import com.nguyen.foodrecipe.repository.CategoryRepository;
import com.nguyen.foodrecipe.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link CategoryService}.
 * <p>
 * All write operations are transactional. Duplicate category names
 * are checked before create and update to provide clear error messages.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        log.debug("Creating category with name: {}", request.name());

        if (categoryRepository.existsByName(request.name())) {
            throw new DuplicateCategoryException(request.name());
        }

        Category category = categoryMapper.toEntity(request);
        Category saved = categoryRepository.save(category);

        log.info("Category created successfully with id: {}", saved.getId());
        return categoryMapper.toResponse(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        log.debug("Updating category id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        // Check for duplicate name, excluding the current category
        categoryRepository.findByName(request.name())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateCategoryException(request.name());
                });

        categoryMapper.updateEntityFromRequest(request, category);
        Category updated = categoryRepository.save(category);

        log.info("Category updated successfully with id: {}", updated.getId());
        return categoryMapper.toResponse(updated);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteCategory(Long id) {
        log.debug("Deleting category id: {}", id);

        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }

        categoryRepository.deleteById(id);
        log.info("Category deleted successfully with id: {}", id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CategoryResponse> getAllCategories() {
        log.debug("Fetching all categories");

        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toResponseList(categories);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoryResponse getCategoryById(Long id) {
        log.debug("Fetching category id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        return categoryMapper.toResponse(category);
    }
}
