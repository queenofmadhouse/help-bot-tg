package eva.bots.bot;

import eva.bots.bot.adminpanel.AdminPanelButtonsHandler;
import eva.bots.bot.adminpanel.AdminPanelHandler;
import eva.bots.bot.callbackqueryhandler.CallBackQueryHandler;
import eva.bots.bot.mainmenu.MainMenuButtonsHandler;
import eva.bots.bot.mainmenu.MainMenuHandler;
import eva.bots.bot.starthandler.StartHandler;
import eva.bots.dto.TelegramMessageDTO;
import eva.bots.exception.TelegramRuntimeException;
import eva.bots.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagesHandler {

    private final MainMenuHandler mainMenuHandler;
    private final StartHandler startHandler;
    private final RequestHandler requestHandler;
    private final AdminPanelHandler adminPanelHandler;
    private final CallBackQueryHandler callBackQueryHandler;
    private final AdminPanelButtonsHandler adminPanelButtonsHandler;
    private final MainMenuButtonsHandler mainMenuButtonsHandler;
    private final AdminService adminService;
    private final Jedis jedis;

    public List<TelegramMessageDTO> handleMessages(Update update) {

        if (update == null) {
            throw new TelegramRuntimeException("Update is null");
        }

        if (update.hasCallbackQuery()) {

            List<SendMessage> sendMessages = callBackQueryHandler.handleCallBackQuery(update);

            return sendMessages.stream().map(
                    sendMessage -> TelegramMessageDTO.builder()
                            .sendMessage(sendMessage)
                            .build()
            ).toList();
        }

        if (update.hasMessage()
                && update.getMessage().getText() != null
                && update.getMessage().getText().equals("/admin")
                && adminService.existByTelegramUserId(update.getMessage().getChatId())) {

            List<SendMessage> sendMessages = adminPanelHandler.provideAdminPanel(update);

            return sendMessages.stream().map(
                    sendMessage -> TelegramMessageDTO.builder()
                            .sendMessage(sendMessage)
                            .build()
            ).toList();
        }

        if (update.hasMessage()
                && update.getMessage().getText() != null
                && update.getMessage().getText().equals("/start")) {

            return startHandler.handleMainMenu(update);
        }

        if (update.hasMessage()
                && update.getMessage().getText() != null
                && update.getMessage().getText().equals("/menu")) {

            return mainMenuHandler.handleMainMenu(update).stream().map(
                    sendMessage -> TelegramMessageDTO.builder()
                            .sendMessage(sendMessage)
                            .build()
            ).toList();
        }

        Message message = update.getMessage();

        if (jedis.get(message.getChatId().toString() + ":state") != null &&
                jedis.get(message.getChatId().toString() + ":state").equals("adm_waiting_for_message")) {

            List<SendMessage> sendMessages = adminPanelButtonsHandler.sendPrivateMessage(
                    Long.parseLong(jedis.get(message.getChatId().toString() + ":requestId")),
                    message.getText());

            jedis.del(message.getChatId().toString() + ":state", message.getChatId().toString() + ":requestId");

            return sendMessages.stream().map(
                    sendMessage -> TelegramMessageDTO.builder()
                            .sendMessage(sendMessage)
                            .build()
            ).toList();
        }

        if (jedis.get(message.getChatId().toString() + ":state") != null &&
                jedis.get(message.getChatId().toString() + ":state").equals("usr_waiting_for_message")) {

            List<SendMessage> sendMessages = mainMenuButtonsHandler.sendPrivateMessage(
                    Long.parseLong(jedis.get(message.getChatId().toString() + ":requestId")),
                    message.getText());

            jedis.del(message.getChatId().toString() + ":state", message.getChatId().toString() + ":requestId");

            return sendMessages.stream().map(
                    sendMessage -> TelegramMessageDTO.builder()
                            .sendMessage(sendMessage)
                            .build()
            ).toList();
        }

        if (jedis.get(message.getChatId().toString() + ":state_waiting_for_user_name") != null) {

            List<SendMessage> sendMessages = requestHandler.handleInputName(update);

            jedis.del(message.getChatId().toString() + ":state_waiting_for_user_name");

            return sendMessages.stream().map(
                    sendMessage -> TelegramMessageDTO.builder()
                            .sendMessage(sendMessage)
                            .build()
            ).toList();
        }

        if (jedis.get(message.getChatId().toString() + ":state_waiting_for_user_pronouns") != null) {

            List<SendMessage> sendMessages = requestHandler.handleInputPronouns(update);

            jedis.del(message.getChatId().toString() + ":state_waiting_for_user_pronouns");

            return sendMessages.stream().map(
                    sendMessage -> TelegramMessageDTO.builder()
                            .sendMessage(sendMessage)
                            .build()
            ).toList();
        }

        if (jedis.get(message.getChatId().toString() + ":state_waiting_for_user_request") != null) {

            List<SendMessage> sendMessages = requestHandler.handleInputRequest(update);

            jedis.del(message.getChatId().toString() + ":state_waiting_for_user_request");

            return sendMessages.stream().map(
                    sendMessage -> TelegramMessageDTO.builder()
                            .sendMessage(sendMessage)
                            .build()
            ).toList();
        }

        throw new TelegramRuntimeException("Can't handle message");
    }
}
