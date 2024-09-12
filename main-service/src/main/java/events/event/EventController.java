package events.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import events.StatsClient;
import events.event.dto.EventFullDto;
import events.event.dto.EventShortDto;
import events.exception.ValidationException;
import events.stats.StatsRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static events.Constants.DATA_PATTERN;
import static events.Constants.DATE_FORMATTER;

@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final StatsClient statsClient;

    @GetMapping
    public List<EventShortDto> findAllPublishedEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATA_PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATA_PATTERN) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            HttpServletRequest httpServletRequest) {
        String ip = httpServletRequest.getRemoteAddr();
        String uri = httpServletRequest.getRequestURI();
        statsClient.addStat(StatsRequestDto.builder()
                .app("events-main-service")
                .ip(ip)
                .uri(uri)
                .timestamp(LocalDateTime.now().format(DATE_FORMATTER))
                .build());
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("RangeEnd is before RangeStart");
        }
        Pageable pageable = sort == null ? PageRequest.of(from, size) : PageRequest.of(from, size, Sort.by(sort).descending());
        return eventService.findAllPublishedEvents(text == null ? "" : text,
                categories == null ? List.of() : categories,
                paid,
                rangeStart == null ? LocalDateTime.now() : rangeStart,
                rangeEnd == null ? LocalDateTime.now().plusYears(100) : rangeEnd,
                onlyAvailable != null && onlyAvailable,
                pageable);
    }

    @GetMapping("{id}")
    public EventFullDto getPublishedEventById(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        String ip = httpServletRequest.getRemoteAddr();
        String uri = httpServletRequest.getRequestURI();
        statsClient.addStat(StatsRequestDto.builder()
                .app("events-main-service")
                .ip(ip)
                .uri(uri)
                .timestamp(LocalDateTime.now().format(DATE_FORMATTER))
                .build());
        return eventService.getPublishedEventById(id);
    }
}
