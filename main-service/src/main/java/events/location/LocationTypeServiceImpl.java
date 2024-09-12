package events.location;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import events.exception.DataIntegrityViolationException;
import events.exception.NotFoundException;
import events.exception.ValidationException;
import events.location.dao.LocationRepository;
import events.location.dao.LocationTypeRepository;
import events.location.dto.LocationTypeDto;
import events.location.dto.NewLocationTypeDto;
import events.location.model.LocationType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationTypeServiceImpl implements LocationTypeService {
    private final LocationTypeRepository locationTypeRepository;
    private final LocationRepository locationRepository;

    @Transactional
    @Override
    public LocationTypeDto createLocationType(NewLocationTypeDto newLocationTypeDto) {
        if (locationTypeRepository.existsByName(newLocationTypeDto.getName())) {
            throw new DataIntegrityViolationException("Location type \"" + newLocationTypeDto.getName() + "\" already exists!");
        }
        return LocationTypeMapper.toLocationTypeDto(
                locationTypeRepository.save(new LocationType(null, newLocationTypeDto.getName()))
        );
    }

    @Transactional
    @Override
    public LocationTypeDto updateLocationType(Long typeId, NewLocationTypeDto newLocationTypeDto) {
        if (newLocationTypeDto.getName().length() < 3 || newLocationTypeDto.getName().length() > 200) {
            throw new ValidationException("LocationType's name length must be >=3 and <=200!");
        }
        LocationType locationType = locationTypeRepository.findById(typeId).orElseThrow(
                () -> new NotFoundException("LocationType (id = " + typeId + ") not found!"));
        locationType.setName(newLocationTypeDto.getName());
        return LocationTypeMapper.toLocationTypeDto(locationTypeRepository.save(locationType));
    }

    @Transactional
    @Override
    public void deleteLocationType(Long typeId) {
        if (typeId == 1) {
            throw new DataIntegrityViolationException("LocationType (id = " + typeId + ") can't be deleted!");

        }
        if (locationRepository.existsByTypeId(typeId)) {
            throw new DataIntegrityViolationException("LocationType (id = " + typeId + ") is used in Locations!");
        }
        locationTypeRepository.deleteById(typeId);
    }

    @Override
    public LocationTypeDto getLocationTypeById(Long typeId) {
        return LocationTypeMapper.toLocationTypeDto(
                locationTypeRepository.findById(typeId).orElseThrow(
                        () -> new NotFoundException("LocationType (id = " + typeId + ") not found!"))
        );
    }

    @Override
    public List<LocationTypeDto> getLocationTypes(String text, Pageable pageable) {
        return LocationTypeMapper.toLocationTypeDtoList(
                locationTypeRepository.findAllByNameContainingIgnoreCase(text, pageable)
        );
    }
}
