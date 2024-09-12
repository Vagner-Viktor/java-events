package events.participation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import static events.Constants.DATA_PATTERN;

@Data
@Builder
public class ParticipationRequestDto {
    private Long id;
    private Long event;
    private Long requester;
    private String status;
    @JsonFormat(pattern = DATA_PATTERN)
    private LocalDateTime created;
}
