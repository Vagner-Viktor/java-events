package events.event;

import org.springframework.data.domain.Pageable;
import events.event.dto.EventFullDto;
import events.event.dto.EventShortDto;
import events.event.dto.NewEventDto;
import events.event.dto.UpdateEventAdminRequest;
import events.event.dto.UpdateEventUserRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getUserEvents(Long userId, Pageable pageable);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByIdAndUserId(Long userId, Long eventId);

    EventFullDto updateEventByIdAndUserId(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto updateEventById(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    EventFullDto getPublishedEventById(Long eventId);

    List<EventShortDto> findAllPublishedEvents(String text,
                                               List<Long> categories,
                                               Boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Boolean onlyAvailable,
                                               Pageable pageable);

    List<EventFullDto> findAllEvents(List<Long> users,
                                     List<String> states,
                                     List<Long> categories,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     String locName,
                                     List<Long> locTypes,
                                     Double locLat,
                                     Double locLon,
                                     Double locRadius,
                                     Pageable pageable);
}
