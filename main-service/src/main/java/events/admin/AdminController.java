package events.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import events.StatsClient;
import events.category.CategoryService;
import events.category.dto.CategoryDto;
import events.category.dto.NewCategoryDto;
import events.compilations.CompilationService;
import events.compilations.dto.CompilationDto;
import events.compilations.dto.NewCompilationDto;
import events.event.EventService;
import events.event.dto.EventFullDto;
import events.event.dto.UpdateEventAdminRequest;
import events.exception.ValidationException;
import events.location.LocationService;
import events.location.LocationTypeService;
import events.location.dto.LocationFullDto;
import events.location.dto.LocationTypeDto;
import events.location.dto.NewLocationDto;
import events.location.dto.NewLocationTypeDto;
import events.stats.StatsRequestDto;
import events.user.UserService;
import events.user.dto.NewUserDto;
import events.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static events.Constants.DATA_PATTERN;
import static events.Constants.DATE_FORMATTER;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/admin")
public class AdminController {
    private final CategoryService categoryService;
    private final UserService userService;
    private final EventService eventService;
    private final CompilationService compilationService;
    private final LocationService locationService;
    private final LocationTypeService locationTypeService;
    private final StatsClient statsClient;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        return categoryService.createCategory(newCategoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @RequestBody NewCategoryDto newCategoryDto) {
        return categoryService.updateCategory(catId, newCategoryDto);
    }

    @GetMapping("/users")
    public List<UserDto> findAll(
            @RequestParam(required = false, value = "ids") List<Long> ids,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        Pageable pageable = PageRequest.of(from, size);
        return userService.findAllByIds(ids, pageable);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserDto newUserDto) {
        return userService.create(newUserDto);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

    @GetMapping("/events")
    public List<EventFullDto> findAllEvent(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATA_PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATA_PATTERN) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "") String locName,
            @RequestParam(required = false) List<Long> locTypes,
            @RequestParam(required = false) Double locLat,
            @RequestParam(required = false) Double locLon,
            @RequestParam(required = false) Double locRadius,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            HttpServletRequest httpServletRequest) {
        String ip = httpServletRequest.getRemoteAddr();
        String uri = httpServletRequest.getRequestURI();
        statsClient.addStat(StatsRequestDto.builder()
                .app("events-main-service")
                .ip(ip)
                .uri(uri)
                .timestamp(LocalDateTime.now().format(DATE_FORMATTER))
                .build());
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("RangeEnd is before RangeStart");
        }
        Pageable pageable = PageRequest.of(from, size);
        return eventService.findAllEvents(users == null ? List.of() : users,
                states == null ? List.of() : states,
                categories == null ? List.of() : categories,
                rangeStart == null ? LocalDateTime.now() : rangeStart,
                rangeEnd == null ? LocalDateTime.now().plusYears(100) : rangeEnd,
                locName == null ? "" : locName,
                locTypes == null ? List.of() : locTypes,
                locLat == null ? 0 : locLat,
                locLon == null ? 0 : locLon,
                locRadius == null ? 1000000 : locRadius,
                pageable);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEventById(@PathVariable Long eventId,
                                        @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEventById(eventId, updateEventAdminRequest);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.createCompilation(newCompilationDto);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.updateCompilation(compId, newCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @GetMapping("/locations")
    public List<LocationFullDto> getLocations(@RequestParam(defaultValue = "") String text,
                                              @RequestParam(required = false) List<Long> types,
                                              @RequestParam(required = false) Double lat,
                                              @RequestParam(required = false) Double lon,
                                              @RequestParam(required = false) Double radiusFrom,
                                              @RequestParam(required = false) Double radiusTo,
                                              @RequestParam(required = false) String sort,
                                              @RequestParam(defaultValue = "0") @Min(0) int from,
                                              @RequestParam(defaultValue = "10") @Min(1) int size) {
        if (radiusFrom != null && radiusTo != null && radiusFrom > radiusTo) {
            throw new ValidationException("radiusTo is before radiusFrom");
        }
        Pageable pageable = sort == null ? PageRequest.of(from, size) : PageRequest.of(from, size, Sort.by(sort).descending());
        if (types == null) types = List.of();
        return locationService.getLocations(text, types, lat, lon, radiusFrom, radiusTo, pageable);
    }

    @GetMapping("/locations/{locId}")
    public LocationFullDto getLocationById(@PathVariable Long locId) {
        return locationService.getLocationById(locId);
    }

    @PostMapping("/locations")
    @ResponseStatus(HttpStatus.CREATED)
    public LocationFullDto createLocation(@Valid @RequestBody NewLocationDto newLocationDto) {
        return locationService.createLocation(newLocationDto);
    }

    @PatchMapping("/locations/{locId}")
    public LocationFullDto updateLocation(@PathVariable Long locId,
                                          @RequestBody NewLocationDto newLocationDto) {
        return locationService.updateLocation(locId, newLocationDto);
    }

    @DeleteMapping("/locations/{locId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocation(@PathVariable Long locId) {
        locationService.deleteLocation(locId);
    }

    @GetMapping("/locations/types")
    public List<LocationTypeDto> getLocationsType(@RequestParam(defaultValue = "") String text,
                                                  @RequestParam(defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(defaultValue = "10") @Min(1) int size) {
        Pageable pageable = PageRequest.of(from, size);
        return locationTypeService.getLocationTypes(text, pageable);
    }

    @GetMapping("/locations/types/{typeId}")
    public LocationTypeDto getLocationTypeById(@PathVariable Long typeId) {
        return locationTypeService.getLocationTypeById(typeId);
    }

    @PostMapping("/locations/types")
    @ResponseStatus(HttpStatus.CREATED)
    public LocationTypeDto createLocationType(@Valid @RequestBody NewLocationTypeDto newLocationTypeDto) {
        return locationTypeService.createLocationType(newLocationTypeDto);
    }

    @PatchMapping("/locations/types/{typeId}")
    public LocationTypeDto updateLocationType(@PathVariable Long typeId,
                                              @RequestBody NewLocationTypeDto newLocationTypeDto) {
        return locationTypeService.updateLocationType(typeId, newLocationTypeDto);
    }

    @DeleteMapping("/locations/types/{typeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocationType(@PathVariable Long typeId) {
        locationTypeService.deleteLocationType(typeId);
    }
}
