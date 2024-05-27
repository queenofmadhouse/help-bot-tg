package eva.bots.service;

import eva.bots.entity.RegularRequest;
import eva.bots.entity.UrgentRequest;
import eva.bots.repository.RegularRequestRepository;
import eva.bots.repository.UrgentRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UrgentRequestService {

    private final UrgentRequestRepository urgentRequestRepository;

    public void save(UrgentRequest request) {
        urgentRequestRepository.save(request);
    }

    public List<UrgentRequest> findAll() {
        return urgentRequestRepository.findAll();
    }
}
