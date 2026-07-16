package com.nguyen.foodrecipe.exception;

public class CannotDisableLastAdminException extends RuntimeException {
    public CannotDisableLastAdminException() {
        super("Cannot disable the last active administrator");
    }
}
