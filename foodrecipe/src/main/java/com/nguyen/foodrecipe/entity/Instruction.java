package com.nguyen.foodrecipe.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity representing a single step within a {@link Recipe}'s instructions.
 * <p>
 * Instructions are managed exclusively through their parent recipe
 * (no standalone CRUD API). They are ordered by {@code stepNumber}.
 * </p>
 */
@Entity
@Table(name = "instructions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instruction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_instruction_recipe"))
    private Recipe recipe;

    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;

    @Column(name = "instruction_text", nullable = false, columnDefinition = "TEXT")
    private String instructionText;
}
