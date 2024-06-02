package eva.bots.bot;

import eva.bots.bot.adminpanel.AdminPanelProvider;
import eva.bots.bot.adminpanel.AdminButtonsHandler;
import eva.bots.bot.callbackqueryhandler.CallBackQueryHandler;
import eva.bots.bot.mainmenu.MainMenuButtonsHandler;
import eva.bots.bot.mainmenu.MainMenuHandler;
import eva.bots.exception.TelegramRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagesHandler {

//    private final Long adminChatId = 225773842L; // FIXME
//    902213751
    private final Long adminChatId = 902213751L; // FIXME

    private final ApplicationEventPublisher eventPublisher;
    private final MainMenuHandler startMessageHandler;
    private final RequestHandler requestHandler;
    private final WebAppHandler webAppHandler;
    private final AdminPanelProvider adminPanelProvider;
    private final CallBackQueryHandler callBackQueryHandler;
    private final AdminButtonsHandler adminButtonsHandler;
    private final MainMenuButtonsHandler mainMenuButtonsHandler;
    private final Jedis jedis;

    public List<SendMessage> handleMessages(Update update) {

        if (update == null) {
            throw new TelegramRuntimeException("Update is null");
        }

        if (update.hasCallbackQuery()) {
            return callBackQueryHandler.handleCallBackQuery(update);
        }

        if (update.hasMessage()
                && update.getMessage().getText() != null
                && update.getMessage().getText().equals("/admin")
                && update.getMessage().getChatId().equals(adminChatId)) {

            return adminPanelProvider.provideAdminPanel(update);
        }

        if (update.hasMessage()
                && update.getMessage().getText() != null
                && update.getMessage().getText().equals("/start")) {

            return startMessageHandler.handleMainMenu(update);
        }

        Message message = update.getMessage();

        if (update.hasMessage() && message.getWebAppData() != null) {
            log.info("Trying to handel webApp data");

            return Collections.singletonList(webAppHandler.handleWebApp(message));
        }

        if (jedis.get(message.getChatId().toString() + ":state") != null &&
                jedis.get(message.getChatId().toString() + ":state").equals("adm_waiting_for_message")) {

            List<SendMessage> sendMessages = adminButtonsHandler.sendPrivateMessage(
                    Long.parseLong(jedis.get(message.getChatId().toString()+ ":requestId")),
                    message.getText());

            jedis.del(message.getChatId().toString() + ":state", message.getChatId().toString() + ":requestId");

            return sendMessages;
        }

        if (jedis.get(message.getChatId().toString() + ":state") != null &&
                jedis.get(message.getChatId().toString() + ":state").equals("usr_waiting_for_message")) {

            List<SendMessage> sendMessages = mainMenuButtonsHandler.sendPrivateMessage(
                    Long.parseLong(jedis.get(message.getChatId().toString()+ ":requestId")),
                    message.getText());

            jedis.del(message.getChatId().toString() + ":state", message.getChatId().toString() + ":requestId");

            return sendMessages;
        }
        throw new TelegramRuntimeException("Can't handle message");
    }
}
