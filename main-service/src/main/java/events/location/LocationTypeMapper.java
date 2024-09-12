package events.location;

import events.location.dto.LocationTypeDto;
import events.location.model.LocationType;

import java.util.List;

public class LocationTypeMapper {

    public static List<LocationTypeDto> toLocationTypeDtoList(List<LocationType> locationTypes) {
        if (locationTypes == null) return List.of();
        return locationTypes.stream().map(LocationTypeMapper::toLocationTypeDto)
                .toList();
    }

    public static LocationTypeDto toLocationTypeDto(LocationType locationType) {
        if (locationType == null) return null;
        return new LocationTypeDto(locationType.getId(), locationType.getName());
    }
}
