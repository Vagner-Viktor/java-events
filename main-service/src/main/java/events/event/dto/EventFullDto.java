package events.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import events.category.dto.CategoryDto;
import events.location.dto.LocationFullDto;
import events.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static events.Constants.DATA_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFullDto {
    private Long id;
    private String title;
    private String description;
    private String annotation;
    private CategoryDto category;
    private UserShortDto initiator;
    @JsonFormat(pattern = DATA_PATTERN)
    private LocalDateTime eventDate;
    @JsonFormat(pattern = DATA_PATTERN)
    private LocalDateTime createdOn;
    @JsonFormat(pattern = DATA_PATTERN)
    private LocalDateTime publishedOn;
    private String state;
    private LocationFullDto location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    private Long views;
    private Long confirmedRequests;
}
