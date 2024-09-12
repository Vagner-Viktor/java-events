package events.location;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import events.event.dao.EventRepository;
import events.exception.DataIntegrityViolationException;
import events.exception.NotFoundException;
import events.exception.ValidationException;
import events.location.dao.LocationRepository;
import events.location.dao.LocationTypeRepository;
import events.location.dto.LocationFullDto;
import events.location.dto.NewLocationDto;
import events.location.model.Location;
import events.location.model.LocationType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final LocationTypeRepository locationTypeRepository;
    private final EventRepository eventRepository;

    @Override
    public LocationFullDto createLocation(NewLocationDto newLocationDto) {
        if (locationRepository.existsByName(newLocationDto.getName())) {
            throw new DataIntegrityViolationException("Location \"" + newLocationDto.getName() + "\" already exists!");
        }
        LocationType locationType = locationTypeRepository.findByName(newLocationDto.getType()).orElseThrow(
                () -> new NotFoundException("LocationType \"" + newLocationDto.getType() + "\" not found!"));
        return LocationMapper.toLocationFullDto(locationRepository.save(Location.builder()
                .name(newLocationDto.getName())
                .type(locationType)
                .lon(newLocationDto.getLon())
                .lat(newLocationDto.getLat())
                .radius(newLocationDto.getRadius())
                .lon(newLocationDto.getLon())
                .build()));
    }

    @Override
    public LocationFullDto updateLocation(Long locId, NewLocationDto newLocationDto) {
        Location location = locationRepository.findById(locId).orElseThrow(
                () -> new NotFoundException("Location (id = " + locId + ") not found!")
        );
        if (newLocationDto.getName() != null) {
            if (newLocationDto.getName().length() < 3 || newLocationDto.getName().length() > 200) {
                throw new ValidationException("Location's name length must be >=3 and <=200!");
            }
            location.setName(newLocationDto.getName());
        }
        if (newLocationDto.getLon() != null) location.setLon(newLocationDto.getLon());
        if (newLocationDto.getLat() != null) location.setLat(newLocationDto.getLat());
        if (newLocationDto.getRadius() != null) {
            if (newLocationDto.getRadius() <= 0) throw new ValidationException("Radius must be positive!");
            location.setRadius(newLocationDto.getRadius());
        }
        if (newLocationDto.getType() != null) {
            if (newLocationDto.getType().length() < 3 || newLocationDto.getType().length() > 200) {
                throw new ValidationException("Location type's name length must be >=3 and <=200!");
            }
            LocationType locationType = locationTypeRepository.findByName(newLocationDto.getType()).orElseThrow(
                    () -> new NotFoundException("LocationType \"" + newLocationDto.getType() + "\" not found!"));
            location.setType(locationType);
        }
        return LocationMapper.toLocationFullDto(location);
    }

    @Override
    public void deleteLocation(Long locId) {
        if (eventRepository.existsByLocationId(locId)) {
            throw new DataIntegrityViolationException("Location (id = " + locId + ") is used in Events!");
        }
        locationRepository.deleteById(locId);
    }

    @Override
    public LocationFullDto getLocationById(Long locId) {
        return LocationMapper.toLocationFullDto(
                locationRepository.findById(locId).orElseThrow(
                        () -> new NotFoundException("Location (id = " + locId + " not found!"))
        );
    }

    @Override
    public List<LocationFullDto> getLocations(String text, List<Long> types, Double lat, Double lon, Double radiusFrom, Double radiusTo, Pageable pageable) {
        return LocationMapper.toLocationFullDtoList(
                locationRepository.findAllLocationsByFilters(text, types, lat, lon, radiusFrom, radiusTo, pageable)
        );
    }
}
