package eva.bots.service;

import eva.bots.entity.Admin;
import eva.bots.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public boolean existByTelegramUserId(Long id) {
        return adminRepository.existsByTelegramUserId(id);
    }

    public List<Admin> findAll() {
        return adminRepository.findAll();
    }
}
