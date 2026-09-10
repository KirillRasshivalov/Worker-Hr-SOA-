package itmo.soa.worker.specification;

import itmo.soa.worker.entity.WorkerEntity;
import itmo.soa.workerhr.model.Country;
import itmo.soa.workerhr.model.EyeColor;
import itmo.soa.workerhr.model.HairColor;
import itmo.soa.workerhr.model.Location;
import itmo.soa.workerhr.model.Person;
import itmo.soa.workerhr.model.Position;
import itmo.soa.workerhr.model.Status;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class WorkerSpecifications {

    private static final Set<String> ALLOWED_FILTERS = Set.of(
            "id",
            "name",
            "salary",
            "position",
            "status",
            "creationDate",
            "coordinates.x",
            "coordinates.y",
            "person.height",
            "person.eyeColor",
            "person.hairColor",
            "person.nationality",
            "person.location.x",
            "person.location.y",
            "person.location.z",
            "person.location.name"
    );

    public static final Set<String> ALLOWED_SORT_FIELDS = ALLOWED_FILTERS;

    private WorkerSpecifications() {
    }

    public static Specification<WorkerEntity> fromFilters(Map<String, String> filters) {
        return (root, query, cb) -> {
            if (filters == null || filters.isEmpty()) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                String field = entry.getKey();
                String raw = entry.getValue();
                if (raw == null || raw.isBlank()) {
                    continue;
                }
                if (!ALLOWED_FILTERS.contains(field)) {
                    throw new IllegalArgumentException("Недопустимое поле фильтра: " + field);
                }
                predicates.add(buildPredicate(root, cb, field, raw.trim()));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    public static Specification<WorkerEntity> personEquivalent(Person person) {
        return (root, query, cb) -> {
            Join<WorkerEntity, ?> personJoin = root.join("person", JoinType.INNER);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(personJoin.get("height"), person.getHeight()));
            predicates.add(cb.equal(personJoin.get("nationality"), person.getNationality()));
            predicates.add(nullSafeEqual(cb, personJoin.get("eyeColor"), person.getEyeColor()));
            predicates.add(nullSafeEqual(cb, personJoin.get("hairColor"), person.getHairColor()));

            Location location = person.getLocation();
            Path<?> locX = personJoin.get("location").get("x");
            Path<?> locY = personJoin.get("location").get("y");
            Path<?> locZ = personJoin.get("location").get("z");
            Path<?> locName = personJoin.get("location").get("name");

            if (location == null) {
                predicates.add(cb.and(
                        cb.isNull(locX),
                        cb.isNull(locY),
                        cb.isNull(locZ),
                        cb.isNull(locName)
                ));
            } else {
                predicates.add(cb.equal(locX, location.getX()));
                predicates.add(cb.equal(locY, location.getY()));
                predicates.add(cb.equal(locZ, location.getZ()));
                predicates.add(nullSafeEqual(cb, locName, location.getName()));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static Predicate nullSafeEqual(CriteriaBuilder cb, Path<?> path, Object value) {
        if (value == null) {
            return cb.isNull(path);
        }
        return cb.equal(path, value);
    }

    private static Predicate buildPredicate(Root<WorkerEntity> root, CriteriaBuilder cb,
                                            String field, String raw) {
        Path<?> path = resolve(root, field);
        Object value = convert(field, raw);
        if (value instanceof String str) {
            return cb.equal(cb.lower(path.as(String.class)), str.toLowerCase());
        }
        return cb.equal(path, value);
    }

    private static Path<?> resolve(Root<WorkerEntity> root, String field) {
        String[] parts = field.split("\\.");
        Path<?> path = root;
        for (String part : parts) {
            path = path.get(part);
        }
        return path;
    }

    private static Object convert(String field, String raw) {
        try {
            return switch (field) {
                case "id", "person.location.x" -> Long.valueOf(raw);
                case "salary", "coordinates.y", "person.location.y" -> Float.valueOf(raw);
                case "coordinates.x" -> Double.valueOf(raw);
                case "person.height", "person.location.z" -> Integer.valueOf(raw);
                case "creationDate" -> LocalDate.parse(raw);
                case "position" -> Position.valueOf(raw);
                case "status" -> Status.valueOf(raw);
                case "person.eyeColor" -> EyeColor.valueOf(raw);
                case "person.hairColor" -> HairColor.valueOf(raw);
                case "person.nationality" -> Country.valueOf(raw);
                case "name", "person.location.name" -> raw;
                default -> throw new IllegalArgumentException("Недопустимое поле фильтра: " + field);
            };
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Некорректное значение фильтра '" + field + "': " + raw);
        }
    }
}
