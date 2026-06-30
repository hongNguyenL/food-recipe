package com.nguyen.foodrecipe.exception;

/**
 * Thrown when attempting to create a category whose name already exists.
 */
public class DuplicateCategoryException extends RuntimeException {

    public DuplicateCategoryException(String name) {
        super("Category already exists with name: " + name);
    }
}
