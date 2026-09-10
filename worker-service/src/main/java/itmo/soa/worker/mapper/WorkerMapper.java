package itmo.soa.worker.mapper;

import itmo.soa.worker.entity.CoordinatesEmbeddable;
import itmo.soa.worker.entity.LocationEmbeddable;
import itmo.soa.worker.entity.PersonEntity;
import itmo.soa.worker.entity.WorkerEntity;
import itmo.soa.workerhr.model.Coordinates;
import itmo.soa.workerhr.model.Location;
import itmo.soa.workerhr.model.Person;
import itmo.soa.workerhr.model.Worker;
import org.springframework.stereotype.Component;

@Component
public class WorkerMapper {

    public Worker toDto(WorkerEntity entity) {
        if (entity == null) {
            return null;
        }

        Worker dto = new Worker();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCoordinates(toDto(entity.getCoordinates()));
        dto.setCreationDate(entity.getCreationDate());
        dto.setSalary(entity.getSalary());
        dto.setPosition(entity.getPosition());
        dto.setStatus(entity.getStatus());
        dto.setPerson(toDto(entity.getPerson()));
        return dto;
    }

    public WorkerEntity toEntity(Worker dto) {
        if (dto == null) {
            return null;
        }
        WorkerEntity entity = new WorkerEntity();
        entity.setName(dto.getName());
        entity.setCoordinates(toEntity(dto.getCoordinates()));
        entity.setSalary(dto.getSalary());
        entity.setPosition(dto.getPosition());
        entity.setStatus(dto.getStatus());
        entity.setPerson(toEntity(dto.getPerson()));
        return entity;
    }

    public void updateEntity(WorkerEntity entity, Worker dto) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setName(dto.getName());
        entity.setCoordinates(toEntity(dto.getCoordinates()));
        entity.setSalary(dto.getSalary());
        entity.setPosition(dto.getPosition());
        entity.setStatus(dto.getStatus());

        if (dto.getPerson() == null) {
            entity.setPerson(null);
        } else if (entity.getPerson() == null) {
            entity.setPerson(toEntity(dto.getPerson()));
        } else {
            updatePerson(entity.getPerson(), dto.getPerson());
        }
    }

    private Coordinates toDto(CoordinatesEmbeddable entity) {
        if (entity == null) {
            return null;
        }
        Coordinates dto = new Coordinates();
        dto.setX(entity.getX());
        dto.setY(entity.getY());
        return dto;
    }

    private CoordinatesEmbeddable toEntity(Coordinates dto) {
        if (dto == null) {
            return null;
        }
        return new CoordinatesEmbeddable(dto.getX(), dto.getY());
    }

    private Person toDto(PersonEntity entity) {
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

    private PersonEntity toEntity(Person dto) {
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

    private void updatePerson(PersonEntity entity, Person dto) {
        entity.setHeight(dto.getHeight());
        entity.setEyeColor(dto.getEyeColor());
        entity.setHairColor(dto.getHairColor());
        entity.setNationality(dto.getNationality());
        entity.setLocation(toEntity(dto.getLocation()));
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
