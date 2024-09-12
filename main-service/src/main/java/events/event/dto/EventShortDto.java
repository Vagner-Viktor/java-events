package events.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import events.category.dto.CategoryDto;
import events.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static events.Constants.DATA_PATTERN;

@Data
@Builder
public class EventShortDto {
    private Long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private UserShortDto initiator;
    @JsonFormat(pattern = DATA_PATTERN)
    private LocalDateTime eventDate;
    private boolean paid;
    private Long views;
    private Long confirmedRequests;
}
