package events.location;

import org.springframework.data.domain.Pageable;
import events.location.dto.LocationFullDto;
import events.location.dto.NewLocationDto;

import java.util.List;

public interface LocationService {
    LocationFullDto createLocation(NewLocationDto newLocationDto);

    LocationFullDto updateLocation(Long locId, NewLocationDto newLocationDto);

    void deleteLocation(Long locId);

    LocationFullDto getLocationById(Long locId);

    List<LocationFullDto> getLocations(String text,
                                       List<Long> types,
                                       Double lat,
                                       Double lon,
                                       Double radiusFrom,
                                       Double radiusTo,
                                       Pageable pageable);
}
