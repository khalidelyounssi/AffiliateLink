package com.affiliateLink.common.exception;

public class InputValidationException extends RuntimeException {

    private final String field;

    public InputValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}