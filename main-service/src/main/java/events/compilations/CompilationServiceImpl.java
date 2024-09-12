package events.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import events.compilations.dao.CompilationRepository;
import events.compilations.dto.CompilationDto;
import events.compilations.dto.NewCompilationDto;
import events.compilations.model.Compilation;
import events.event.dao.EventRepository;
import events.event.model.Event;
import events.exception.NotFoundException;
import events.exception.ValidationException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = List.of();
        if (newCompilationDto.getEvents() != null) {
            events = eventRepository.findAllById(newCompilationDto.getEvents());
        }
        Compilation compilation = Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned() != null && newCompilationDto.getPinned())
                .events(events)
                .build();
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(()
                -> new NotFoundException("Compilation (id =" + compId + ") not found!"));
        if (newCompilationDto.getTitle() != null) {
            if (newCompilationDto.getTitle().isEmpty() ||
                    newCompilationDto.getTitle().length() > 50) {
                throw new ValidationException("Title length must be >=1 and <=50!");
            } else compilation.setTitle(newCompilationDto.getTitle());
        }
        if (newCompilationDto.getPinned() != null) {
            compilation.setPinned(newCompilationDto.getPinned());
        }
        if (newCompilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllById(newCompilationDto.getEvents()));
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation (id =" + compId + ") not found!");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> findAll(Boolean pinned, Pageable pageable) {
        return CompilationMapper.toCompilationDtoList(
                compilationRepository.findAllByPinned(pinned != null && pinned, pageable)
        );
    }

    @Override
    public CompilationDto findById(Long compId) {
        return CompilationMapper.toCompilationDto(compilationRepository.findById(compId).orElseThrow(()
                -> new NotFoundException("Complication (id=" + compId + ") not found!")));
    }
}
