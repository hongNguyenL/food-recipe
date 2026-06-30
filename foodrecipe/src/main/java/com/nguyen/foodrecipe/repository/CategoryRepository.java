package com.nguyen.foodrecipe.repository;

import com.nguyen.foodrecipe.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Category} entities.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds a category by its exact name (case-sensitive).
     *
     * @param name the category name
     * @return an optional containing the category if found
     */
    Optional<Category> findByName(String name);

    /**
     * Checks whether a category with the given name already exists.
     *
     * @param name the category name
     * @return {@code true} if a category with that name exists
     */
    boolean existsByName(String name);
}
