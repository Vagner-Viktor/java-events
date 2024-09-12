package events.compilations.dto;

import lombok.Builder;
import lombok.Data;
import events.event.dto.EventShortDto;

import java.util.List;

@Data
@Builder
public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;
}
