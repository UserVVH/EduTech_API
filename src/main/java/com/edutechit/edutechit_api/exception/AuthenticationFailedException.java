package com.edutechit.edutechit_api.exception;

public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException(String message) {
        super(message);
    }
}