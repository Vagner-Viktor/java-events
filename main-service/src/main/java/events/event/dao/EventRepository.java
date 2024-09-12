package events.event.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import events.event.model.Event;
import events.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends PagingAndSortingRepository<Event, Long>, JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByInitiatorIdAndId(Long userId, Long eventId);

    boolean existsByCategoryId(Long categoryId);

    Optional<Event> findByIdAndState(Long eventId, EventState state);

    @Query(value = "SELECT * " +
            "FROM Events AS e " +
            "WHERE ((e.annotation ILIKE %:text% OR e.description ILIKE %:text%) " +
            "AND (e.category_id IN (:categories) OR :categories IS NULL) " +
            "AND (e.paid = CAST(:paid AS boolean) OR :paid IS NULL) " +
            "AND (e.event_date BETWEEN :rangeStart AND :rangeEnd ) " +
            "AND (CAST(:onlyAvailable AS BOOLEAN) is TRUE " +
            "  OR( " +
            "  SELECT count(id) " +
            "  FROM participation_requests AS r " +
            "  WHERE r.event_id = e.id) < e.participant_limit) " +
            "AND state = 'PUBLISHED') ",
            nativeQuery = true)
    List<Event> findAllPublishedEventsByFilters(@Param("text") String text,
                                                @Param("categories") List<Long> categories,
                                                @Param("paid") Boolean paid,
                                                @Param("rangeStart") LocalDateTime rangeStart,
                                                @Param("rangeEnd") LocalDateTime rangeEnd,
                                                @Param("onlyAvailable") Boolean onlyAvailable,
                                                Pageable pageable);

    @Query(value = "SELECT e.id, e.title, e.annotation, e.description, e.category_id, e.initiator_id, e.event_date, e.created_on, e.published_on, e.state, e.location_id, e.paid, e.participant_limit, e.request_moderation " +
            "FROM Events AS e " +
            "LEFT JOIN locations AS l ON e.location_id = l.id " +
            "LEFT JOIN location_types AS lt ON l.type_id = lt.id " +
            "WHERE ((e.state IN (:states) OR :states IS NULL) " +
            "AND (e.category_id IN (:categories) OR :categories IS NULL) " +
            "AND (e.initiator_id IN (:users) OR :users IS NULL) " +
            "AND (e.event_date BETWEEN :rangeStart AND :rangeEnd) " +
            "AND (lt.name ILIKE %:locName%) " +
            "AND (l.type_id IN (:locTypes) OR :locTypes IS NULL) " +
            "AND (distance(l.lat, l.lon, :locLat, :locLon) <= :locRadius + l.radius) OR :locLat IS NULL OR :locLon IS NULL OR :locRadius IS NULL) " +
            "ORDER BY e.id ASC",
            nativeQuery = true)
    List<Event> findAllEventsByFilters(@Param("users") List<Long> users,
                                       @Param("states") List<String> states,
                                       @Param("categories") List<Long> categories,
                                       @Param("rangeStart") LocalDateTime rangeStart,
                                       @Param("rangeEnd") LocalDateTime rangeEnd,
                                       @Param("locName") String locName,
                                       @Param("locTypes") List<Long> locTypes,
                                       @Param("locLat") Double locLat,
                                       @Param("locLon") Double locLon,
                                       @Param("locRadius") Double locRadius,
                                       Pageable pageable);

    boolean existsByLocationId(Long locId);
}
