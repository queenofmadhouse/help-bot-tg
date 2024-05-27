package eva.bots.repository;

import eva.bots.entity.UrgentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrgentRequestRepository extends JpaRepository<UrgentRequest, Long> {
}
