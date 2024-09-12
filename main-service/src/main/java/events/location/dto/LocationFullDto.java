package events.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class LocationFullDto {
    private Long id;
    private String name;
    private String type;
    private double lat;
    private double lon;
    private double radius;
}
