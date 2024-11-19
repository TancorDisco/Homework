package ru.sweetbun.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String className, Long id) {
        super(className + " not found with id: " + id);
    }

    public ResourceNotFoundException(String className, String title) {
        super(className + " not found with title: " + title);
    }
}
