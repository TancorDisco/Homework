package ru.sweetbun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sweetbun.entity.Place;

import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query("SELECT p FROM Place p LEFT JOIN FETCH p.events WHERE p.id = :id")
    Optional<Place> findPlaceById();

    Place findPlaceBySlug(String slug);
}
