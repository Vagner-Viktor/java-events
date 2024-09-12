package events.event.dto;

import lombok.Data;
import events.event.model.EventState;
import events.location.model.Location;

@Data
public class UpdateEventUserRequest {
    private String title;
    private String annotation;
    private String description;
    private Long category;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private EventState stateAction;
}
