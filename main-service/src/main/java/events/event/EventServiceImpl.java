package events.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import events.StatsClient;
import events.category.dao.CategoryRepository;
import events.category.model.Category;
import events.event.dao.EventRepository;
import events.event.dto.EventFullDto;
import events.event.dto.EventShortDto;
import events.event.dto.NewEventDto;
import events.event.dto.UpdateEventAdminRequest;
import events.event.dto.UpdateEventUserRequest;
import events.event.model.Event;
import events.event.model.EventState;
import events.exception.DataIntegrityViolationException;
import events.exception.NotFoundException;
import events.exception.StatsClientException;
import events.exception.ValidationException;
import events.location.dao.LocationRepository;
import events.location.dao.LocationTypeRepository;
import events.location.model.Location;
import events.location.model.LocationType;
import events.stats.StatsDto;
import events.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static events.Constants.DATE_FORMATTER;
import static events.Constants.DEFAULT_LOCATION_RADIUS;
import static events.Constants.DEFAULT_LOCATION_TYPE_ID;

@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final LocationTypeRepository locationTypeRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Pageable pageable) {
        validation(userId, null, null);
        return EventMapper.toEventShortDtoList(eventRepository.findAllByInitiatorId(userId, pageable));
    }

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        validation(userId, null, newEventDto);
        Optional<Location> location = locationRepository.findByLatAndLon(newEventDto.getLocation().getLat(), newEventDto.getLocation().getLon());
        if (location.isPresent()) newEventDto.setLocation(location.get());
        else {
            LocationType locationType = locationTypeRepository.findById(DEFAULT_LOCATION_TYPE_ID)
                    .orElseThrow(() -> new NotFoundException("Defaul LocationType not found!"));
            Location newLocation = locationRepository.save(Location.builder()
                    .name(newEventDto.getTitle())
                    .type(locationType)
                    .lat(newEventDto.getLocation().getLat())
                    .lon(newEventDto.getLocation().getLon())
                    .radius(DEFAULT_LOCATION_RADIUS)
                    .build());
            newEventDto.setLocation(newLocation);
        }
        return EventMapper.toEventFullDto(
                eventRepository.save(EventMapper.toEvent(userId, newEventDto))
        );
    }

    @Override
    public EventFullDto getEventByIdAndUserId(Long userId, Long eventId) {
        return EventMapper.toEventFullDto(
                eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(() ->
                        new NotFoundException("Event (id = " + eventId + ") or user (id = " + userId + ") not found!"))
        );
    }

    @Transactional
    @Override
    public EventFullDto updateEventByIdAndUserId(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        LocalDateTime eventDate = null;
        if (updateEventUserRequest.getEventDate() != null) {
            eventDate = LocalDateTime.parse(updateEventUserRequest.getEventDate(), DATE_FORMATTER);
            if (eventDate.minusHours(2).isBefore(LocalDateTime.now())) {
                throw new ValidationException("You cannot update an event earlier than 2 hours before it!");
            }
        }
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(() ->
                new NotFoundException("Event (id = " + eventId + ") or user (id = " + userId + ") not found!"));
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new DataIntegrityViolationException("Event status is PUBLISHED!");
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            if (updateEventUserRequest.getAnnotation().length() < 20 ||
                    updateEventUserRequest.getAnnotation().length() > 2000) {
                throw new ValidationException("Annotation length must be >=20 and <=2000!");
            }
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategory()).orElseThrow(() ->
                    new NotFoundException("Category (id = " + updateEventUserRequest.getCategory() + ") not found!"));
            event.setCategory(category);
        }
        if (updateEventUserRequest.getDescription() != null) {
            if (updateEventUserRequest.getDescription().length() < 20 ||
                    updateEventUserRequest.getDescription().length() > 7000) {
                throw new ValidationException("Description length must be >=20 and <=7000!");
            }
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(eventDate);
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.getLocation().setLat(updateEventUserRequest.getLocation().getLat());
            event.getLocation().setLon(updateEventUserRequest.getLocation().getLon());
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            if (updateEventUserRequest.getParticipantLimit() <= 0) {
                throw new ValidationException("Participant limit not positive!");
            }
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getStateAction() != null) {
            if (event.getState().equals(EventState.PUBLISHED))
                throw new DataIntegrityViolationException("Event status is PUBLISHED!");
            if (updateEventUserRequest.getStateAction().equals(EventState.SEND_TO_REVIEW) &&
                    event.getState().equals(EventState.CANCELED)) {
                event.setState(EventState.PENDING);
            } else if (updateEventUserRequest.getStateAction().equals(EventState.CANCEL_REVIEW) &&
                    event.getState().equals(EventState.PENDING)) {
                event.setState(EventState.CANCELED);
            } else if (updateEventUserRequest.getStateAction().equals(EventState.REJECT_EVENT) &&
                    event.getState().equals(EventState.PENDING)) {
                event.setState(EventState.REJECTED);
            } else if (updateEventUserRequest.getStateAction().equals(EventState.PUBLISH_EVENT) &&
                    event.getState().equals(EventState.PENDING)) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else throw new DataIntegrityViolationException("Event status is not PENDING!");
        }
        if (updateEventUserRequest.getTitle() != null) {
            if (updateEventUserRequest.getTitle().length() < 3 ||
                    updateEventUserRequest.getTitle().length() > 120) {
                throw new ValidationException("Title length must be >=3 and <=120!");
            }
            event.setTitle(updateEventUserRequest.getTitle());
        }
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Transactional
    @Override
    public EventFullDto updateEventById(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        LocalDateTime eventDate = null;
        if (updateEventAdminRequest.getEventDate() != null) {
            eventDate = LocalDateTime.parse(updateEventAdminRequest.getEventDate(), DATE_FORMATTER);
            if (eventDate.minusHours(1).isBefore(LocalDateTime.now())) {
                throw new ValidationException("You cannot update an event earlier than 1 hours before it!");
            }
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event (id = " + eventId + ") not found!"));
        if (updateEventAdminRequest.getAnnotation() != null) {
            if (updateEventAdminRequest.getAnnotation().length() < 20 ||
                    updateEventAdminRequest.getAnnotation().length() > 2000) {
                throw new ValidationException("Annotation length must be >=20 and <=2000!");
            }
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategory()).orElseThrow(() ->
                    new NotFoundException("Category (id = " + updateEventAdminRequest.getCategory() + ") not found!"));
            event.setCategory(category);
        }
        if (updateEventAdminRequest.getDescription() != null) {
            if (updateEventAdminRequest.getDescription().length() < 20 ||
                    updateEventAdminRequest.getDescription().length() > 7000) {
                throw new ValidationException("Description length must be >=20 and <=7000!");
            }
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(eventDate);
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.getLocation().setLat(updateEventAdminRequest.getLocation().getLat());
            event.getLocation().setLon(updateEventAdminRequest.getLocation().getLon());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            if (updateEventAdminRequest.getParticipantLimit() <= 0) {
                throw new ValidationException("Participant limit not positive!");
            }
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (event.getState().equals(EventState.PUBLISHED))
                throw new DataIntegrityViolationException("Event status is PUBLISHED!");
            if (updateEventAdminRequest.getStateAction().equals(EventState.CANCEL_REVIEW) &&
                    event.getState().equals(EventState.PENDING)) {
                event.setState(EventState.CANCELED);
            } else if (updateEventAdminRequest.getStateAction().equals(EventState.REJECT_EVENT) &&
                    event.getState().equals(EventState.PENDING)) {
                event.setState(EventState.CANCELED);
            } else if (updateEventAdminRequest.getStateAction().equals(EventState.PUBLISH_EVENT) &&
                    event.getState().equals(EventState.PENDING)) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else throw new DataIntegrityViolationException("Event status is not PENDING!");
        }
        if (updateEventAdminRequest.getTitle() != null) {
            if (updateEventAdminRequest.getTitle().length() < 3 ||
                    updateEventAdminRequest.getTitle().length() > 120) {
                throw new ValidationException("Title length must be >=3 and <=120!");
            }
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getPublishedEventById(Long eventId) {
        EventFullDto eventFullDto = EventMapper.toEventFullDto(
                eventRepository.findByIdAndState(eventId, EventState.PUBLISHED).orElseThrow(()
                        -> new NotFoundException("Event (id = " + eventId + ") not found!")));
        List<StatsDto> statsDtos = getEventsStats(List.of("/events/" + eventFullDto.getId()));
        if (statsDtos != null)
            eventFullDto.setViews(statsDtos.getLast().getHits());
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> findAllPublishedEvents(String text,
                                                      List<Long> categories,
                                                      Boolean paid,
                                                      LocalDateTime rangeStart,
                                                      LocalDateTime rangeEnd,
                                                      Boolean onlyAvailable,
                                                      Pageable pageable) {
        List<EventShortDto> eventShortDtos = EventMapper.toEventShortDtoList(
                eventRepository.findAllPublishedEventsByFilters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable)
        );
        Map<String, EventShortDto> uris = new HashMap<>();
        eventShortDtos.forEach(eventShortDto -> uris.put("/events/" + eventShortDto.getId(), eventShortDto));
        List<StatsDto> statsDtos = getEventsStats(uris.keySet().stream().toList());
        if (statsDtos != null)
            statsDtos.forEach(statsDto -> uris.get(statsDto.getUri()).setViews(statsDto.getHits()));
        return uris.values().stream().toList();
    }

    @Override
    public List<EventFullDto> findAllEvents(List<Long> users,
                                            List<String> states,
                                            List<Long> categories,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            String locName,
                                            List<Long> locTypes,
                                            Double locLat,
                                            Double locLon,
                                            Double locRadius,
                                            Pageable pageable) {
        List<EventFullDto> eventFullDtos = EventMapper.toEventFullDtoList(
                eventRepository.findAllEventsByFilters(users, states, categories, rangeStart, rangeEnd,
                        locName, locTypes, locLat, locLon, locRadius, pageable)
        );
        if (eventFullDtos == null || eventFullDtos.isEmpty()) return List.of();
        Map<String, EventFullDto> uris = new HashMap<>();
        eventFullDtos.forEach(eventFullDto -> uris.put("/events/" + eventFullDto.getId(), eventFullDto));
        List<StatsDto> statsDtos = getEventsStats(uris.keySet().stream().toList());
        if (statsDtos != null)
            statsDtos.forEach(statsDto -> uris.get(statsDto.getUri()).setViews(statsDto.getHits()));
        return uris.values().stream().toList();
    }

    private void validation(Long userId, Long eventId, NewEventDto newEventDto) {
        if (newEventDto != null) {
            if (!categoryRepository.existsById(newEventDto.getCategory())) {
                throw new ValidationException("Category id = " + newEventDto.getCategory() + " not found!");
            }
            LocalDateTime eventDate = LocalDateTime.parse(newEventDto.getEventDate(), DATE_FORMATTER);
            if (eventDate.minusHours(2).isBefore(LocalDateTime.now())) {
                throw new ValidationException("You cannot create an event earlier than 2 hours before it!");
            }
        }
        if (userId != null && !userRepository.existsById(userId)) {
            throw new ValidationException("User id = " + userId + " not found!");
        }
        if (eventId != null && !eventRepository.existsById(eventId)) {
            throw new ValidationException("Event id = " + eventId + " not found!");
        }
    }

    private List<StatsDto> getEventsStats(List<String> uris) {

        ResponseEntity<List<StatsDto>> response = statsClient.getStats(
                LocalDateTime.now().minusYears(10),
                LocalDateTime.now().plusYears(10),
                uris,
                true);
        if (response.getStatusCode().is4xxClientError()) {
            throw new StatsClientException("Bad request. Status code is: " + response.getStatusCode());
        }
        if (response.getStatusCode().is5xxServerError()) {
            throw new StatsClientException("Internal server error statusCode is " + response.getStatusCode());
        }
        if (response.getBody() == null) {
            throw new StatsClientException("Returned empty body");
        }
        return response.getBody();
    }
}
