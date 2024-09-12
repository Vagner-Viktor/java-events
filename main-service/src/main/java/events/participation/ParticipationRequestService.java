package events.participation;

import events.event.dto.EventRequestStatusUpdateRequest;
import events.event.dto.EventRequestStatusUpdateResult;
import events.participation.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto createUserRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto cancelUserRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsByUserIdAndEventId(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsByUserIdAndEventId(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
