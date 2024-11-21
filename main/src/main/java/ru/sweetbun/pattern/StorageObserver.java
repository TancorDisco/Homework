package ru.sweetbun.pattern;

public interface StorageObserver<T> {
    void onEntityCreated(T entity);
    void onEntityUpdated(Long id, T entity);
    void onEntityDeleted(Long id);
}