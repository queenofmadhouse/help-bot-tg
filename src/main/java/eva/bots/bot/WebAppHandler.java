package eva.bots.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eva.bots.dto.WebAppDataDTO;
import eva.bots.entity.Request;
import eva.bots.exception.TelegramRuntimeException;
import eva.bots.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebAppHandler {

    @Value("${app.constants.bot.bot-admin-id}")
    private Long adminChatId;

    private final ApplicationEventPublisher eventPublisher;
    private final RequestService requestService;
    private final Jedis jedis;

    public SendMessage handleWebApp(Message message) {

        String webAppData = message.getWebAppData().getData();

        try {
            System.out.println("we are in handleWebApp");
            ObjectMapper objectMapper = new ObjectMapper();
            WebAppDataDTO data = objectMapper.readValue(webAppData, WebAppDataDTO.class);

            Long userChatId = message.getChatId();
            String userName = data.getUserName();
            String userPronouns = data.getUserPronouns();
            String userRequest = data.getUserRequest();

            String requestType = jedis.get(userChatId.toString());


            SendMessage response = new SendMessage();
            response.setChatId(message.getChatId().toString());

            if ("urgent".equals(requestType)) {
                Request urgentRequest = Request.builder()
                        .tgChatId(userChatId)
                        .userName(userName)
                        .userPronouns(userPronouns)
                        .requestDate(LocalDateTime.now())
                        .requestText(userRequest)
                        .isUrgent(true)
                        .build();

                log.info("request is: " + urgentRequest);
                requestService.save(urgentRequest);

                response.setText("Спасибо, что обратились ко мне! Я свяжусь с вами как можно быстрее.");

                SendMessage event = new SendMessage();
                event.setChatId(adminChatId);
                event.setText("Пришла новая срочная заявка!");

                log.info("Publishing urgent request event: {}", event);
                eventPublisher.publishEvent(event);
            } else if ("regular".equals(requestType)) {
                System.out.println("we are in regular request");
                Request regularRequest = Request.builder()
                        .tgChatId(userChatId)
                        .userName(userName)
                        .userPronouns(userPronouns)
                        .requestDate(LocalDateTime.now())
                        .requestText(userRequest)
                        .isUrgent(false)
                        .build();

                requestService.save(regularRequest);

                response.setText("Спасибо, что обратились ко мне! Я отвечу вам в течение 24 часов, в будние дни.");
            }

            System.out.println("response is: " + response);
            return (response);
        } catch (JsonProcessingException e) {

            log.error("Ошибка при десериализации JSON: {}", e.getMessage());

            SendMessage errorMessage = new SendMessage();
            errorMessage.setChatId(message.getChatId().toString());
            errorMessage.setText("Произошла ошибка при обработке данных.");

            throw new TelegramRuntimeException("Ошибка при обработке WebApp");
        }
    }
}
