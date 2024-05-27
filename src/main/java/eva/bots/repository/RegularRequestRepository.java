package eva.bots.repository;

import eva.bots.entity.RegularRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegularRequestRepository extends JpaRepository<RegularRequest, Long> {
}
