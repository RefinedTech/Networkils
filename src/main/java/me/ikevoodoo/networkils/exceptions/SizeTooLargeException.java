package me.ikevoodoo.networkils.exceptions;

public class SizeTooLargeException extends Exception {

    public SizeTooLargeException(String message, int size) {
        super(message + " (Size: " + size + " bytes)");
    }

}
