package com.nguyen.foodrecipe.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity representing a single ingredient line within a {@link Recipe}.
 * <p>
 * Ingredients are managed exclusively through their parent recipe
 * (no standalone CRUD API).
 * </p>
 */
@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ingredient_recipe"))
    private Recipe recipe;

    @Column(name = "ingredient_text", nullable = false)
    private String ingredientText;
}
