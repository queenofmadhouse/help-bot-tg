package eva.bots.service;

import eva.bots.entity.Request;
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
                new RuntimeException("exp"));
    }

    public void deleteById(Long id) {
        requestRepository.deleteById(id);
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
