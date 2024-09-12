package events.event.dto;

import lombok.Data;
import events.participation.dto.ParticipationRequestDto;

import java.util.ArrayList;
import java.util.List;

@Data
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
    private List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
}
