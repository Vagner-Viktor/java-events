package events.participation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import events.event.dao.EventRepository;
import events.event.dto.EventRequestStatusUpdateRequest;
import events.event.dto.EventRequestStatusUpdateResult;
import events.event.model.Event;
import events.event.model.EventState;
import events.exception.DataIntegrityViolationException;
import events.exception.NotFoundException;
import events.participation.dao.ParticipationRequestRepository;
import events.participation.dto.ParticipationRequestDto;
import events.participation.model.ParticipationRequest;
import events.participation.model.RequestStatus;
import events.user.dao.UserRepository;
import events.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository pRequestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ParticipationRequestDto createUserRequest(Long userId, Long eventId) {
        if (pRequestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new DataIntegrityViolationException("Request already exists!");
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User (id = " + userId + ") not found!"));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event (id = " + eventId + ") not found!"));
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new DataIntegrityViolationException("Event initiator cannot add request!");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new DataIntegrityViolationException("Event not published!");
        }
        if (!event.isRequestModeration() && event.getParticipantLimit() != 0) {
            if (pRequestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
                throw new DataIntegrityViolationException("Event participant Limit!");
            }
        }
        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .requester(user)
                .event(event)
                .status(!event.isRequestModeration() || event.getParticipantLimit() == 0 ? RequestStatus.CONFIRMED : RequestStatus.PENDING)
                .build();
        return ParticipationRequestMapper.toParticipationRequestDto(
                pRequestRepository.save(participationRequest)
        );
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User (id = " + userId + ") not found!");
        }
        return ParticipationRequestMapper.toParticipationRequestDtoList(
                pRequestRepository.findAllByRequesterId(userId)
        );
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        ParticipationRequest participationRequest = pRequestRepository.findByRequesterIdAndId(userId, requestId).orElseThrow(() ->
                new NotFoundException("Request (id = " + requestId + ") not found!"));
        participationRequest.setStatus(RequestStatus.CANCELED);
        return ParticipationRequestMapper.toParticipationRequestDto(
                pRequestRepository.save(participationRequest)
        );
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUserIdAndEventId(Long userId, Long eventId) {
        return ParticipationRequestMapper.toParticipationRequestDtoList(
                pRequestRepository.findAllByEventInitiatorIdAndEventId(userId, eventId)
        );
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestsByUserIdAndEventId(Long userId, Long eventId, EventRequestStatusUpdateRequest eRSURequest) {
        if (eRSURequest.getRequestIds() == null) {
            throw new DataIntegrityViolationException("RequestIds is null!");
        }
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(()
                -> new NotFoundException("Event not found!"));
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0)
            return new EventRequestStatusUpdateResult();
        List<ParticipationRequest> pRequests =
                pRequestRepository.findAllByEventInitiatorIdAndEventIdAndIdIn(userId, eventId, eRSURequest.getRequestIds());
        if (eRSURequest.getRequestIds().size() != pRequests.size()) {
            throw new DataIntegrityViolationException("Not all request correct!");
        }
        EventRequestStatusUpdateResult eRSUResult = new EventRequestStatusUpdateResult();
        RequestStatus curEventState = eRSURequest.getStatus();
        long curEventRequests = pRequestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (curEventRequests >= event.getParticipantLimit()) {
            throw new DataIntegrityViolationException("Event limit reached!");
        }
        for (ParticipationRequest pRequest : pRequests) {
            if (pRequest.getStatus() != RequestStatus.PENDING) {
                throw new DataIntegrityViolationException("Request (id = " + pRequest.getId() + ") hat not PENDING status!");
            }
            if (curEventState == RequestStatus.CONFIRMED) {
                pRequest.setStatus(RequestStatus.CONFIRMED);
                eRSUResult.getConfirmedRequests().add(ParticipationRequestMapper.toParticipationRequestDto(pRequest));
                curEventRequests++;
                if (curEventRequests >= event.getParticipantLimit()) curEventState = RequestStatus.REJECTED;
            } else if (curEventState == RequestStatus.REJECTED) {
                pRequest.setStatus(RequestStatus.REJECTED);
                eRSUResult.getRejectedRequests().add(ParticipationRequestMapper.toParticipationRequestDto(pRequest));
            }
        }
        pRequestRepository.saveAll(pRequests);
        return eRSUResult;
    }
}
