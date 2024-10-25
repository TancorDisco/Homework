package ru.sweetbun.storage;

public interface StorageObserver<T> {
    void onEntityCreated(T entity);
    void onEntityUpdated(Long id, T entity);
    void onEntityDeleted(Long id);
}