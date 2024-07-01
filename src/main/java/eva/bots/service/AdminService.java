package eva.bots.service;

import eva.bots.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public boolean existByTelegramUserId(Long id) {
        return adminRepository.existsByTelegramUserId(id);
    }
}
