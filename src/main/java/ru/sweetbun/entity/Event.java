package ru.sweetbun.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import ru.sweetbun.pattern.Memento;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event implements Identifiable{

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "price")
    private String price;

    @JsonProperty("favorites_count")
    @Column(name = "favorites_count")
    private int favoritesCount;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    public Double getPriceAsDouble() {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(price);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group());
        }
        return 0.0;
    }

    @Override
    public Memento<Event> saveToMemento() {
        return new Memento<>(new Event(this.id, this.title, this.price, this.favoritesCount, this.date, this.place));
    }

    public void restoreFromMemento(Memento<Event> memento) {
        Event savedState = memento.getState();
        this.id = savedState.getId();
        this.title = savedState.getTitle();
        this.price = savedState.getPrice();
        this.favoritesCount = savedState.getFavoritesCount();
        this.date = savedState.getDate();
        this.place = savedState.getPlace();
    }}
