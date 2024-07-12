package eva.bots.bot;

import eva.bots.entity.Request;
import eva.bots.service.AdminService;
import eva.bots.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestHandler {

    private final Jedis jedis;
    private final AdminService adminService;
    private final RequestService requestService;

    public SendMessage handleUrgentRequest(Long chatId) {

        jedis.set(chatId.toString() + ":state_waiting_for_user_name", "urgent");

        return createMessage(chatId);
    }

    public SendMessage handleRegularRequest(Long chatId) {

        jedis.set(chatId.toString() + ":state_waiting_for_user_name", "regular");
        return createMessage(chatId);
    }

    public List<SendMessage> handleInputName(Update update) {

        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String userName = message.getText();

        String type = jedis.get(message.getChatId().toString() + ":state_waiting_for_user_name");

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Отлично, теперь укажите пожалуйста предпочтительные местоимения:");
        sendMessage.setChatId(chatId);

        jedis.set(chatId.toString() + ":user_name", userName);
        jedis.set(chatId.toString() + ":state_waiting_for_user_pronouns", type);

        return Collections.singletonList(sendMessage);
    }

    public List<SendMessage> handleInputPronouns(Update update) {

        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String userPronouns = message.getText();

        String type = jedis.get(message.getChatId().toString() + ":state_waiting_for_user_pronouns");

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Отлично, теперь напишите, пожалуйста, ваш запрос в одном сообщении. " +
                "Ко мне придет только одно первое ваше сообщение, поэтому внимательно проверьте ваш запрос пред отправкой" + "\n" +
                "Ваш запрос:");
        sendMessage.setChatId(chatId);

        jedis.set(chatId.toString() + ":user_pronouns", userPronouns);
        jedis.set(chatId.toString() + ":state_waiting_for_user_request", type);

        return Collections.singletonList(sendMessage);
    }

    public List<SendMessage> handleInputRequest(Update update) {

        List<SendMessage> sendMessages = new ArrayList<>();
        Message message = update.getMessage();
        String type = jedis.get(message.getChatId().toString() + ":state_waiting_for_user_request");

        Long chatId = message.getChatId();
        String userName = jedis.get(chatId.toString() + ":user_name");
        String userPronouns = jedis.get(chatId.toString() + ":user_pronouns");
        String userRequest = message.getText();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (type.equals("urgent")) {
            sendMessage.setText("Получил ваш запрос. Спасибо, что обратились ко мне! Я свяжусь с вами как можно быстрее.");
            sendMessages.addAll(prepareNotifications());

            Request urgentRequest = Request.builder()
                    .tgChatId(chatId)
                    .userName(userName)
                    .userPronouns(userPronouns)
                    .requestDate(LocalDateTime.now())
                    .requestText(userRequest)
                    .isUrgent(true)
                    .build();

            requestService.save(urgentRequest);
        } else {
            sendMessage.setText("Получил ваш запрос. Спасибо, что обратились ко мне! Я отвечу вам в течение 24 часов, в будние дни.");

            Request regularRequest = Request.builder()
                    .tgChatId(chatId)
                    .userName(userName)
                    .userPronouns(userPronouns)
                    .requestDate(LocalDateTime.now())
                    .requestText(userRequest)
                    .isUrgent(false)
                    .build();

            requestService.save(regularRequest);
        }

        sendMessages.add(sendMessage);

        jedis.del(chatId.toString() + ":user_name");
        jedis.del(chatId.toString() + ":user_pronouns");

        return sendMessages;
    }

    private SendMessage createMessage(Long chatId) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Перед тем, как создать заявку, мне нужно задать несколько вопросов.\n" +
                "\n" +
                "Скажите, как я могу к вам обращаться?" + "\n" +
                "(Обычным сообщением)");
        sendMessage.enableMarkdown(true);

        return sendMessage;
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
