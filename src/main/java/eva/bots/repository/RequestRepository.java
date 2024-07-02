package eva.bots.repository;

import eva.bots.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByisUrgentIsFalseAndInTheArchiveIsFalseAndInWorkIsFalse();

    List<Request> findAllByisUrgentIsTrueAndInTheArchiveIsFalseAndInWorkIsFalse();
    List<Request> findAllByrelatedAdminIdAndInTheArchiveIsFalse(Long relatedAdminId);
    List<Request> findAllBytgChatId(Long tgChatId);
}
