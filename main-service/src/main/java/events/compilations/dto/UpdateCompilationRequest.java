package events.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UpdateCompilationRequest {
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
    private Boolean pinned;
    private List<Long> events;
}
