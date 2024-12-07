package com.edutechit.edutechit_api.dto;

public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    // Getter và Setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
