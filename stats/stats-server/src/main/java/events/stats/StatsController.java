package events.stats;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import events.StatsConstants;
import events.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping(StatsConstants.API_HIT_PREFIX)
    @ResponseStatus(HttpStatus.CREATED)
    public StatsRequestDto addStats(@Valid @RequestBody StatsRequestDto statsRequestDto) {
        return statsService.addStat(statsRequestDto);
    }

    @GetMapping(StatsConstants.API_STATS_PREFIX)
    public List<StatsDto> getStats(@RequestParam("start") @DateTimeFormat(pattern = StatsConstants.DATA_PATTERN) LocalDateTime start,
                                   @RequestParam("end") @DateTimeFormat(pattern = StatsConstants.DATA_PATTERN) LocalDateTime end,
                                   @RequestParam(required = false, value = "uris") List<String> uris,
                                   @RequestParam(required = false, value = "unique") Boolean unique) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new ValidationException("RangeEnd is before RangeStart");
        }
        return statsService.getStats(start, end, uris, unique);
    }
}
