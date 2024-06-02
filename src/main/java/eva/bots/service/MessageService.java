package eva.bots.service;

import eva.bots.entity.Message;
import eva.bots.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;

    public List<Message> findAllMessagesRelatedToRequest(Long id) {
        return messageRepository.findAllByRequestId(id);
    }

    public void save(Message message) {
        messageRepository.save(message);
    }
}
