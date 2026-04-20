package com.smartcampus.exception;

/**
 * PART 5.2 - Thrown when creating a sensor via POST and the specified roomId does not exist.
 * The reference in the JSON payload is invalid.
 */
public class LinkedResourceNotFoundException extends RuntimeException {

    private final String fieldName;
    private final String fieldValue;

    public LinkedResourceNotFoundException(String fieldName, String fieldValue) {
        super("Referenced resource not found: " + fieldName + " = '" + fieldValue + "'");
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() { return fieldName; }
    public String getFieldValue() { return fieldValue; }
}
