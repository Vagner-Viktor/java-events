package events.compilations;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import events.compilations.dto.CompilationDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/compilations")
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> findAll(@RequestParam(required = false, value = "pinned") Boolean pinned,
                                        @RequestParam(defaultValue = "0") @Min(0) int from,
                                        @RequestParam(defaultValue = "10") @Min(1) int size) {
        Pageable pageable = PageRequest.of(from, size);
        return compilationService.findAll(pinned, pageable);
    }

    @GetMapping("/{compId}")
    public CompilationDto findById(@PathVariable Long compId) {
        return compilationService.findById(compId);
    }
}
