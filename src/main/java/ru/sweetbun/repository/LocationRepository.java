package ru.sweetbun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    public Location findLocationBySlug(String slug);
}
