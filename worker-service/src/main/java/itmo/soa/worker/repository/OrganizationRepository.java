package itmo.soa.worker.repository;

import itmo.soa.worker.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long> {
}
