package eva.bots.service;

import eva.bots.entity.RegularRequest;
import eva.bots.repository.RegularRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegularRequestService {

    private final RegularRequestRepository regularRequestRepository;

    public void save(RegularRequest request) {
        regularRequestRepository.save(request);
    }

    public List<RegularRequest> findAll() {
        return regularRequestRepository.findAll();
    }
}
