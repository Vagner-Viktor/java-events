package events.compilations;

import events.compilations.dto.CompilationDto;
import events.compilations.model.Compilation;
import events.event.EventMapper;

import java.util.List;

public class CompilationMapper {

    public static List<CompilationDto> toCompilationDtoList(List<Compilation> compilations) {
        if (compilations == null) return List.of();
        return compilations.stream()
                .map(CompilationMapper::toCompilationDto)
                .toList();
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        if (compilation == null) return null;
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(EventMapper.toEventShortDtoList(compilation.getEvents()))
                .build();
    }
}
