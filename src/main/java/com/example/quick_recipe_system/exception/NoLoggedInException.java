package com.example.quick_recipe_system.exception;

public class NoLoggedInException extends RuntimeException {

    public NoLoggedInException(String message) {
        super(message);
    }
}
