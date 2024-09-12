package events.location.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewLocationDto {
    @NotBlank
    @Size(min = 3, max = 200)
    private String name;
    @NotBlank
    @Size(min = 3, max = 200)
    private String type;
    private Double lat;
    private Double lon;
    @Positive
    private Double radius;
}
