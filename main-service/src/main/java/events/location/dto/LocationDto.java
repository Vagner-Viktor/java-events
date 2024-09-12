package events.location.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LocationDto {
    private double lat;
    private double lon;
}
