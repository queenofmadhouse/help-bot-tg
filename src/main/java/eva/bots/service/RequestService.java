package eva.bots.service;

import eva.bots.bot.TelegramBot;
import eva.bots.entity.Request;
import eva.bots.exception.DatabaseRuntimeException;
import eva.bots.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void acceptRequest(Long requestId, Long adminId) {
        Request request = findById(requestId);

        request.setInWork(true);
        request.setRelatedAdminId(adminId);

        SendMessage event = new SendMessage();
        event.setChatId(request.getTgChatId());
        event.setText("Заявка принята в обработку");

        applicationEventPublisher.publishEvent(event);

        save(request);
    }

    public void denyRequest(Long requestId) {
        Request request = findById(requestId);

        request.setInTheArchive(true);

        SendMessage event = new SendMessage();
        event.setChatId(request.getTgChatId());
        event.setText("Ваша заявка откланена");

        applicationEventPublisher.publishEvent(event);

        save(request);
    }

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
