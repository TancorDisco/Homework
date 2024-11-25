package ru.sweetbun.pattern;

import ru.sweetbun.entity.Identifiable;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager<T extends Identifiable> {
    private final List<Memento<T>> history = new ArrayList<>();

    public void addMemento(Memento<T> memento) {
        history.add(memento);
    }

    public Memento<T> getMemento(int index) {
        return history.get(index);
    }

    public List<Memento<T>> getHistory() {
        return new ArrayList<>(history);
    }
}