package events.location;

import events.location.dto.LocationDto;
import events.location.dto.LocationFullDto;
import events.location.model.Location;

import java.util.List;

public class LocationMapper {

    public static List<LocationFullDto> toLocationFullDtoList(List<Location> locations) {
        if (locations == null) return List.of();
        return locations.stream().map(LocationMapper::toLocationFullDto)
                .toList();
    }

    public static LocationFullDto toLocationFullDto(Location location) {
        if (location == null) return null;
        return LocationFullDto.builder()
                .id(location.getId())
                .name(location.getName())
                .type(location.getType().getName())
                .lat(location.getLat())
                .lon(location.getLon())
                .radius(location.getRadius())
                .build();
    }

    public static LocationDto toLocationDto(Location location) {
        if (location == null) return null;
        return new LocationDto(location.getLat(), location.getLon());
    }
}
