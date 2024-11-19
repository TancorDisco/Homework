package ru.sweetbun.pattern;

import ru.sweetbun.entity.Identifiable;

public class Memento<T extends Identifiable> {
    private final T state;

    public Memento(T state) {
        this.state = state;
    }

    public T getState() {
        return state;
    }
}