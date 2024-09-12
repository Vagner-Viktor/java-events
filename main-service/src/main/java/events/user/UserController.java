package events.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import events.event.EventService;
import events.event.dto.EventFullDto;
import events.event.dto.EventRequestStatusUpdateRequest;
import events.event.dto.EventRequestStatusUpdateResult;
import events.event.dto.EventShortDto;
import events.event.dto.NewEventDto;
import events.event.dto.UpdateEventUserRequest;
import events.participation.ParticipationRequestService;
import events.participation.dto.ParticipationRequestDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final EventService eventService;
    private final ParticipationRequestService participationRequestService;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getUserEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        Pageable pageable = PageRequest.of(from, size);
        return eventService.getUserEvents(userId, pageable);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByIdAndUserId(@PathVariable Long userId,
                                              @PathVariable Long eventId) {
        return eventService.getEventByIdAndUserId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventByIdAndUserId(@PathVariable Long userId,
                                                 @PathVariable Long eventId,
                                                 @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateEventByIdAndUserId(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByUserIdAndEventId(@PathVariable Long userId,
                                                                       @PathVariable Long eventId) {
        return participationRequestService.getRequestsByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsByUserIdAndEventId(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return participationRequestService.updateRequestsByUserIdAndEventId(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createUserRequest(@PathVariable Long userId,
                                                     @RequestParam Long eventId) {
        return participationRequestService.createUserRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        return participationRequestService.getUserRequests(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelUserRequest(@PathVariable Long userId,
                                                     @PathVariable Long requestId) {
        return participationRequestService.cancelUserRequest(userId, requestId);
    }
}
