package com.edutechit.edutechit_api.exception;

public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException(String message) {
        super(message);
    }
}