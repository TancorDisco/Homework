package ru.sweetbun.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sweetbun.pattern.Memento;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@Table(name = "locations")
@NoArgsConstructor
@AllArgsConstructor
public class Location implements Identifiable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug")
    private String slug;

    @Column(name = "name")
    private String name;

    @JsonManagedReference
    @OneToMany(mappedBy = "location", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Place> places = new ArrayList<>();

    public Memento<Location> saveToMemento() {
        return new Memento<>(new Location(this.id, this.slug, this.name, this.places));
    }

    public void restoreFromMemento(Memento<Location> memento) {
        Location savedState = memento.getState();
        this.id = savedState.getId();
        this.slug = savedState.getSlug();
        this.name = savedState.getName();
        this.places = savedState.getPlaces();
    }
}
