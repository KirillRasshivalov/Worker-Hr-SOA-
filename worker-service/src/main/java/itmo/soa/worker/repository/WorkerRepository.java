package itmo.soa.worker.repository;

import itmo.soa.worker.entity.WorkerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface WorkerRepository extends JpaRepository<WorkerEntity, Long>, JpaSpecificationExecutor<WorkerEntity> {

    Optional<WorkerEntity> findFirstBySalary(float salary);
}
