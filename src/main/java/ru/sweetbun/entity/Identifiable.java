package ru.sweetbun.entity;

import ru.sweetbun.pattern.Memento;

public interface Identifiable {
    Long getId();
    void setId(Long id);

    <T extends Identifiable> Memento<T> saveToMemento();
}
