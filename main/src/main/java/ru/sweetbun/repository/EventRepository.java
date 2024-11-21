package ru.sweetbun.repository;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.sweetbun.entity.Event;
import ru.sweetbun.entity.Place;

import java.time.LocalDate;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    static Specification<Event> hasTitle(String title) {
        return ((root, query, criteriaBuilder) -> title == null
                ? null : criteriaBuilder.equal(root.get("title"), title));
    }

    static Specification<Event> hasPlace(Place place) {
        return (root, query, criteriaBuilder) -> {
            if (place == null) return null;
            root.fetch("place", JoinType.LEFT);
            return criteriaBuilder.equal(root.get("place"), place);
        };
    }

    static Specification<Event> hasDateAfter(LocalDate fromDate) {
        return ((root, query, criteriaBuilder) -> fromDate == null
                ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("date"), fromDate));
    }

    static Specification<Event> hasDateBefore(LocalDate toData) {
        return ((root, query, criteriaBuilder) -> toData == null
                ? null : criteriaBuilder.lessThanOrEqualTo(root.get("date"), toData));
    }
 }
