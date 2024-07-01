package eva.bots.bot;

import eva.bots.bot.adminpanel.AdminButtonsHandler;
import eva.bots.bot.adminpanel.AdminPanelProvider;
import eva.bots.bot.callbackqueryhandler.CallBackQueryHandler;
import eva.bots.bot.mainmenu.MainMenuButtonsHandler;
import eva.bots.bot.mainmenu.MainMenuHandler;
import eva.bots.bot.starthandler.StartHandler;
import eva.bots.dto.TelegramMessageDTO;
import eva.bots.exception.TelegramRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagesHandler {

    @Value("${app.constants.bot.bot-admin-id}")
    private Long adminChatId;
    private final ApplicationEventPublisher eventPublisher;
    private final MainMenuHandler mainMenuHandler;
    private final StartHandler startHandler;
    private final RequestHandler requestHandler;
    private final WebAppHandler webAppHandler;
    private final AdminPanelProvider adminPanelProvider;
    private final CallBackQueryHandler callBackQueryHandler;
    private final AdminButtonsHandler adminButtonsHandler;
    private final MainMenuButtonsHandler mainMenuButtonsHandler;
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
                && update.getMessage().getChatId().equals(adminChatId)) {

            List<SendMessage> sendMessages = adminPanelProvider.provideAdminPanel(update);

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

            List<SendMessage> sendMessages = mainMenuHandler.handleMainMenu(update);

            return sendMessages.stream().map(
                    sendMessage -> TelegramMessageDTO.builder()
                            .sendMessage(sendMessage)
                            .build()
            ).toList();
        }

        Message message = update.getMessage();

        if (update.hasMessage() && message.getWebAppData() != null) {
            return webAppHandler.handleWebApp(message).stream()
                    .map(sendMessage -> TelegramMessageDTO.builder()
                            .sendMessage(sendMessage)
                            .build())
                    .collect(Collectors.toList());
        }

        if (jedis.get(message.getChatId().toString() + ":state") != null &&
                jedis.get(message.getChatId().toString() + ":state").equals("adm_waiting_for_message")) {

            System.out.println("message from admin detected");
            List<SendMessage> sendMessages = adminButtonsHandler.sendPrivateMessage(
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

            System.out.println("message from user detected");

            List<SendMessage> sendMessages = mainMenuButtonsHandler.sendPrivateMessage(
                    Long.parseLong(jedis.get(message.getChatId().toString() + ":requestId")),
                    message.getText());

            System.out.println(sendMessages);

            jedis.del(message.getChatId().toString() + ":state", message.getChatId().toString() + ":requestId");

            return sendMessages.stream().map(
                    sendMessage -> TelegramMessageDTO.builder()
                            .sendMessage(sendMessage)
                            .build()
            ).toList();
        }
        throw new TelegramRuntimeException("Can't handle message");
    }
}
