package events.location.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import events.location.model.Location;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends PagingAndSortingRepository<Location, Long>, JpaRepository<Location, Long> {
    Optional<Location> findByLatAndLon(double lat, double lon);

    boolean existsByTypeId(Long typeId);

    boolean existsByName(String name);

    @Query(value = "SELECT * " +
            "FROM Locations AS l " +
            "WHERE ((l.name ILIKE %:text%) " +
            "AND (l.type_id IN (:types) OR :types IS NULL) " +
            "AND (l.lat = (:lat) OR :lat IS NULL) " +
            "AND (l.lon = (:lon) OR :lon IS NULL) " +
            "AND (l.radius BETWEEN :radiusFrom AND :radiusTo OR :radiusFrom IS NULL OR :radiusTo IS NULL)) ", nativeQuery = true)
    List<Location> findAllLocationsByFilters(@Param("text") String text,
                                             @Param("types") List<Long> types,
                                             @Param("lat") Double lat,
                                             @Param("lon") Double lon,
                                             @Param("radiusFrom") Double radiusFrom,
                                             @Param("radiusTo") Double radiusTo,
                                             Pageable pageable);
}
