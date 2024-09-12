package events;

import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import events.stats.StatsDto;
import events.stats.StatsRequestDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static events.StatsConstants.API_HIT_PREFIX;
import static events.StatsConstants.API_STATS_PREFIX;
import static events.StatsConstants.DATE_FORMATTER;

@Service
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<List<StatsDto>> getStats(LocalDateTime start, LocalDateTime end,
                                                   @Nullable List<String> uris, boolean unique) {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "start", start.format(DATE_FORMATTER),
                "end", end.format(DATE_FORMATTER),
                "unique", unique));
        if (uris != null) {
            for (String s : uris) {
                parameters.put("uris", s);
            }
        }
        return getList(API_STATS_PREFIX + "?start={start}&end={end}&uris={uris}&unique={unique}",
                parameters,
                new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<Object> addStat(StatsRequestDto statsRequestDto) {
        return post(API_HIT_PREFIX, statsRequestDto);
    }
}
