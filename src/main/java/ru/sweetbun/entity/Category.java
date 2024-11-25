package ru.sweetbun.entity;

import lombok.Builder;
import lombok.Data;
import ru.sweetbun.pattern.Memento;

@Data
@Builder
public class Category implements Identifiable {
    private Long id;
    private String slug;
    private String name;

    public Memento<Category> saveToMemento() {
        return new Memento<>(new Category(this.id, this.slug, this.name));
    }

    public void restoreFromMemento(Memento<Category> memento) {
        Category savedState = memento.getState();
        this.id = savedState.getId();
        this.slug = savedState.getSlug();
        this.name = savedState.getName();
    }
}
