package events.event;

import events.category.CategoryMapper;
import events.category.model.Category;
import events.event.dto.EventFullDto;
import events.event.dto.EventShortDto;
import events.event.dto.NewEventDto;
import events.event.model.Event;
import events.event.model.EventState;
import events.location.LocationMapper;
import events.participation.model.RequestStatus;
import events.user.UserMapper;
import events.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static events.Constants.DATE_FORMATTER;

public class EventMapper {

    public static List<EventShortDto> toEventShortDtoList(List<Event> events) {
        if (events.isEmpty()) return List.of();
        return events.stream()
                .map(EventMapper::toEventShortDto)
                .toList();
    }

    public static List<EventFullDto> toEventFullDtoList(List<Event> events) {
        if (events.isEmpty()) return List.of();
        return events.stream()
                .map(EventMapper::toEventFullDto)
                .toList();
    }

    public static EventFullDto toEventFullDto(Event event) {
        if (event == null) return null;
        Long confirmedRequests = 0L;
        if (event.getRequests() != null)
            confirmedRequests = event.getRequests().stream()
                    .filter(participationRequest -> participationRequest.getStatus().equals(RequestStatus.CONFIRMED))
                    .count();
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .eventDate(event.getEventDate())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .state(event.getState().toString())
                .location(LocationMapper.toLocationFullDto(event.getLocation()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .views(0L)
                .confirmedRequests(confirmedRequests)
                .build();
    }

    public static Event toEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto == null) return null;
        return Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .category(new Category(newEventDto.getCategory(), null, null))
                .location(newEventDto.getLocation())
                .requestModeration(newEventDto.getRequestModeration() == null || newEventDto.getRequestModeration())
                .participantLimit(newEventDto.getParticipantLimit() == null ? 0 : newEventDto.getParticipantLimit())
                .paid(newEventDto.getPaid() != null && newEventDto.getPaid())
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(), DATE_FORMATTER))
                .initiator(new User(userId, null, null, null))
                .createdOn(LocalDateTime.now())
                .state(EventState.PENDING)
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        if (event == null) return null;
        Long confirmedRequests = 0L;
        if (event.getRequests() != null)
            confirmedRequests = event.getRequests().stream()
                    .filter(participationRequest -> participationRequest.getStatus().equals(RequestStatus.CONFIRMED))
                    .count();
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .eventDate(event.getEventDate())
                .paid(event.isPaid())
                .confirmedRequests(confirmedRequests)
                .views(0L)
                .build();
    }
}
