package events.participation;

import events.participation.dto.ParticipationRequestDto;
import events.participation.model.ParticipationRequest;

import java.util.List;

public class ParticipationRequestMapper {

    public static List<ParticipationRequestDto> toParticipationRequestDtoList(List<ParticipationRequest> participationRequests) {
        if (participationRequests == null) return List.of();
        return participationRequests.stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .toList();
    }

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        if (participationRequest == null) return null;
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .created(participationRequest.getCreated())
                .requester(participationRequest.getRequester().getId())
                .event(participationRequest.getEvent().getId())
                .status(participationRequest.getStatus().toString())
                .build();
    }
}
