package events.location.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import events.location.model.LocationType;

import java.util.List;
import java.util.Optional;

public interface LocationTypeRepository extends PagingAndSortingRepository<LocationType, Long>, JpaRepository<LocationType, Long> {
    boolean existsByName(String name);

    Optional<LocationType> findByName(String name);

    List<LocationType> findAllByNameContainingIgnoreCase(String text, Pageable pageable);
}
