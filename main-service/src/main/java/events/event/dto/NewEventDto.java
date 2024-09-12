package events.event.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import events.location.model.Location;

@Data
public class NewEventDto {
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    private Long category;

    @NotNull
    private String eventDate;

    @NotNull
    private Location location;

    private final Boolean requestModeration;
    @Min(value = 0)
    private final Integer participantLimit;
    private final Boolean paid;
}
