package events.location;

import org.springframework.data.domain.Pageable;
import events.location.dto.LocationTypeDto;
import events.location.dto.NewLocationTypeDto;

import java.util.List;

public interface LocationTypeService {
    LocationTypeDto createLocationType(NewLocationTypeDto newLocationTypeDto);

    LocationTypeDto updateLocationType(Long typeId, NewLocationTypeDto newLocationTypeDto);

    void deleteLocationType(Long typeId);

    LocationTypeDto getLocationTypeById(Long typeId);

    List<LocationTypeDto> getLocationTypes(String text, Pageable pageable);
}
