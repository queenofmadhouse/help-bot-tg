package eva.bots.controller;

import eva.bots.entity.Message;
import eva.bots.entity.Request;
import eva.bots.service.MessageService;
import eva.bots.service.RequestService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;
import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class MainController {

    private final RequestService requestService;
    private final MessageService messageService;

    @RequestMapping("/api/request/urgent")
    public List<Request> getUrgentRequests() {
        return requestService.findAllUrgent();
    }

    @RequestMapping("/api/request/regular")
    public List<Request> getRegularRequests() {
        return requestService.findAllRegular();
    }

    @RequestMapping("/api/request/my")
    public List<Request> getMyRequest() {
        return requestService.findAllByRelatedAdminId(225773842L);
    }

    @RequestMapping("/api/message/get/{id}")
    public List<Message> getMessages(@PathVariable long id) {
        return messageService.findAllMessagesRelatedToRequest(id);
    }

    @RequestMapping("/api/message/send")
    public void sendMessage(@RequestBody Message message) {
        messageService.save(message);
    }
}
