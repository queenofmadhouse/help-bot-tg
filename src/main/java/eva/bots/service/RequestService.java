package eva.bots.service;

import eva.bots.entity.Request;
import eva.bots.exception.DatabaseRuntimeException;
import eva.bots.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    public void save(Request request) {
        requestRepository.save(request);
    }

    public Request findById(Long id) {
        return requestRepository.findById(id).orElseThrow(() ->
                new DatabaseRuntimeException("Can't find request by id: " + id));
    }

    public List<Request> findAllUrgent() {
        return requestRepository.findAllByisUrgentIsTrueAndInTheArchiveIsFalseAndInWorkIsFalse();
    }

    public List<Request> findAllRegular() {
        return requestRepository.findAllByisUrgentIsFalseAndInTheArchiveIsFalseAndInWorkIsFalse();
    }

    public List<Request> findAllByRelatedAdminId(Long id) {
        return requestRepository.findAllByrelatedAdminIdAndInTheArchiveIsFalse(id);
    }

    public List<Request> findAllByRelatedUserId(Long id) {
        return requestRepository.findAllBytgChatId(id);
    }
}
