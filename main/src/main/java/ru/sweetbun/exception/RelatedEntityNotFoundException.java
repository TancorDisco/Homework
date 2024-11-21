package ru.sweetbun.exception;

public class RelatedEntityNotFoundException extends RuntimeException {
    public RelatedEntityNotFoundException(String message) {
        super(message);
    }

    public RelatedEntityNotFoundException(String className, String slug) {
        super("No such " + className + " was found in the database for slug: " + slug);
    }
}
