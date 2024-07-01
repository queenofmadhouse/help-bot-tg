package eva.bots.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eva.bots.dto.WebAppDataDTO;
import eva.bots.entity.Request;
import eva.bots.exception.TelegramRuntimeException;
import eva.bots.service.AdminService;
import eva.bots.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebAppHandler {

    private final RequestService requestService;
    private final AdminService adminService;
    private final Jedis jedis;

    public List<SendMessage> handleWebApp(Message message) {

        String webAppData = message.getWebAppData().getData();

        try {
            List<SendMessage> sendMessages = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            WebAppDataDTO data = objectMapper.readValue(webAppData, WebAppDataDTO.class);

            Long userChatId = message.getChatId();
            String userName = data.getUserName();
            String userPronouns = data.getUserPronouns();
            String userRequest = data.getUserRequest();
            String requestType = jedis.get(userChatId.toString());

            SendMessage messageToUser = new SendMessage();
            messageToUser.setChatId(message.getChatId().toString());

            if ("urgent".equals(requestType)) {

                Request urgentRequest = Request.builder()
                        .tgChatId(userChatId)
                        .userName(userName)
                        .userPronouns(userPronouns)
                        .requestDate(LocalDateTime.now())
                        .requestText(userRequest)
                        .isUrgent(true)
                        .build();

                requestService.save(urgentRequest);

                messageToUser.setText("Спасибо, что обратились ко мне! Я свяжусь с вами как можно быстрее.");

                sendMessages.addAll(prepareNotifications());
            } else if ("regular".equals(requestType)) {

                Request regularRequest = Request.builder()
                        .tgChatId(userChatId)
                        .userName(userName)
                        .userPronouns(userPronouns)
                        .requestDate(LocalDateTime.now())
                        .requestText(userRequest)
                        .isUrgent(false)
                        .build();

                requestService.save(regularRequest);

                messageToUser.setText("Спасибо, что обратились ко мне! Я отвечу вам в течение 24 часов, в будние дни.");
            }

            sendMessages.add(messageToUser);

            return sendMessages;
        } catch (JsonProcessingException e) {

            log.error("Ошибка при десериализации JSON: {}", e.getMessage());

            SendMessage errorMessage = new SendMessage();
            errorMessage.setChatId(message.getChatId().toString());
            errorMessage.setText("Произошла ошибка при обработке данных.");

            throw new TelegramRuntimeException("Ошибка при обработке WebApp");
        }
    }

    private List<SendMessage> prepareNotifications() {

        return adminService.findAll().stream()
                .map(admin -> SendMessage.builder()
                        .chatId(admin.getTelegramUserId())
                        .text("Пришла новая срочная заявка!")
                        .build())
                .collect(Collectors.toList());
    }
}
