package events.compilations;

import org.springframework.data.domain.Pageable;
import events.compilations.dto.CompilationDto;
import events.compilations.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Long compId, NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    List<CompilationDto> findAll(Boolean pinned, Pageable pageable);

    CompilationDto findById(Long compId);
}
