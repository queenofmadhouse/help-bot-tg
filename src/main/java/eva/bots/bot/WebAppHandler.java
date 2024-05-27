package eva.bots.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eva.bots.dto.WebAppDataDTO;
import eva.bots.entity.RegularRequest;
import eva.bots.entity.UrgentRequest;
import eva.bots.exception.TelegramRuntimeException;
import eva.bots.service.RegularRequestService;
import eva.bots.service.UrgentRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebAppHandler {

    private final Long adminChatId = 225773842L; // FIXME
    private final ApplicationEventPublisher eventPublisher;
    private final UrgentRequestService urgentRequestService;
    private final RegularRequestService regularRequestService;
    private final Jedis jedis;

    public SendMessage handleWebApp(Message message) {

        String webAppData = message.getWebAppData().getData();

        try {
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
                UrgentRequest urgentRequest = UrgentRequest.builder()
                        .tgChatId(userChatId)
                        .userName(userName)
                        .userPronouns(userPronouns)
                        .requestDate(LocalDateTime.now())
                        .requestText(userRequest)
                        .build();

                log.info("request is: " + urgentRequest);
                urgentRequestService.save(urgentRequest);

                response.setText("Срочная заявка принята в обработку");

                SendMessage event = new SendMessage();
                event.setChatId(adminChatId);
                event.setText("Пришла новая срочная заявка!");

                eventPublisher.publishEvent(event);


            } else if ("regular".equals(requestType)) {
                RegularRequest regularRequest = RegularRequest.builder()
                        .tgChatId(userChatId)
                        .userName(userName)
                        .requestDate(LocalDateTime.now())
                        .requestText(userRequest)
                        .build();

                regularRequestService.save(regularRequest);

                response.setText("Заявка принята в обработку");
            }

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
