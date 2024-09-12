package events.participation.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import events.participation.model.ParticipationRequest;
import events.participation.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByRequesterId(Long userId);

    Optional<ParticipationRequest> findByRequesterIdAndId(Long userId, Long requestId);

    List<ParticipationRequest> findAllByEventInitiatorIdAndEventId(Long userId, Long eventId);

    List<ParticipationRequest> findAllByEventInitiatorIdAndEventIdAndIdIn(Long userId, Long eventId, List<Long> requestIds);
}
