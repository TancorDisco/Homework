package ru.sweetbun.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
