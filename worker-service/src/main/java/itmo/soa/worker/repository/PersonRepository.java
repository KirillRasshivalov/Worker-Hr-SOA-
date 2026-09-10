package itmo.soa.worker.repository;

import itmo.soa.worker.entity.PersonEntity;
import itmo.soa.workerhr.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    @Query("""
            SELECT p FROM PersonEntity p
            WHERE p.height = :#{#person.height}
              AND p.eyeColor = :#{#person.eyeColor}
              AND p.hairColor = :#{#person.hairColor}
              AND p.nationality = :#{#person.nationality}
              AND p.location.x = :#{#person.location.x}
              AND p.location.y = :#{#person.location.y}
              AND p.location.z = :#{#person.location.z}
              AND p.location.name = :#{#person.location.name}
            """)
    List<PersonEntity> findAllMatching(@Param("person") Person person);
}
