package dev.refinedtech.networkils.exceptions;

public class UnexpectedSizeException extends Exception {

    public UnexpectedSizeException(String message, int size) {
        super(message + " (Size: " + size + " bytes)");
    }

}
