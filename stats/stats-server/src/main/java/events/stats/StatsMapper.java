package events.stats;

import events.StatsConstants;
import events.stats.model.Stats;

import java.time.LocalDateTime;

public class StatsMapper {

    public static Stats toStats(StatsRequestDto statsRequestDto) {
        return Stats.builder()
                .app(statsRequestDto.getApp())
                .ip(statsRequestDto.getIp())
                .uri(statsRequestDto.getUri())
                .timestamp(LocalDateTime.parse(statsRequestDto.getTimestamp(), StatsConstants.DATE_FORMATTER))
                .build();
    }

    public static StatsRequestDto toStatsRequestDto(Stats stats) {
        return StatsRequestDto.builder()
                .app(stats.getApp())
                .ip(stats.getIp())
                .uri(stats.getUri())
                .timestamp(stats.getTimestamp().toString())
                .build();
    }
}
