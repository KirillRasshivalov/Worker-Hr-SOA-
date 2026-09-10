package itmo.soa.worker.mapper;

import itmo.soa.worker.entity.LocationEmbeddable;
import itmo.soa.worker.entity.PersonEntity;
import itmo.soa.workerhr.model.Country;
import itmo.soa.workerhr.model.EyeColor;
import itmo.soa.workerhr.model.HairColor;
import itmo.soa.workerhr.model.Location;
import itmo.soa.workerhr.model.Person;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    public Person toDto(PersonEntity entity) {
        if (entity == null) {
            return null;
        }
        Person dto = new Person();
        dto.setHeight(entity.getHeight());
        dto.setEyeColor(entity.getEyeColor());
        dto.setHairColor(entity.getHairColor());
        dto.setNationality(entity.getNationality());
        dto.setLocation(toDto(entity.getLocation()));
        return dto;
    }

    public PersonEntity toEntity(Person dto) {
        if (dto == null) {
            return null;
        }
        PersonEntity entity = new PersonEntity();
        entity.setHeight(dto.getHeight());
        entity.setEyeColor(dto.getEyeColor());
        entity.setHairColor(dto.getHairColor());
        entity.setNationality(dto.getNationality());
        entity.setLocation(toEntity(dto.getLocation()));
        return entity;
    }

    public Person fromQueryParams(
            Integer height,
            EyeColor eyeColor,
            HairColor hairColor,
            Country nationality,
            Long locationX,
            Float locationY,
            Integer locationZ,
            String locationName
    ) {
        Person person = new Person();
        person.setHeight(height);
        person.setEyeColor(eyeColor);
        person.setHairColor(hairColor);
        person.setNationality(nationality);

        if (locationX != null || locationY != null || locationZ != null || locationName != null) {
            Location location = new Location();
            location.setX(locationX != null ? locationX : 0L);
            location.setY(locationY != null ? locationY : 0f);
            location.setZ(locationZ != null ? locationZ : 0);
            location.setName(locationName);
            person.setLocation(location);
        }
        return person;
    }

    private Location toDto(LocationEmbeddable entity) {
        if (entity == null) {
            return null;
        }
        Location dto = new Location();
        dto.setX(entity.getX() != null ? entity.getX() : 0L);
        dto.setY(entity.getY() != null ? entity.getY() : 0f);
        dto.setZ(entity.getZ() != null ? entity.getZ() : 0);
        dto.setName(entity.getName());
        return dto;
    }

    private LocationEmbeddable toEntity(Location dto) {
        if (dto == null) {
            return null;
        }
        return new LocationEmbeddable(dto.getX(), dto.getY(), dto.getZ(), dto.getName());
    }
}
