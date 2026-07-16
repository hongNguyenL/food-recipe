package com.nguyen.foodrecipe.exception;

public class SelfRoleRemovalException extends RuntimeException {
    public SelfRoleRemovalException() {
        super("Administrators cannot remove their own ADMIN role");
    }
}
