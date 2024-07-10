package eva.bots.bot.mainmenu;

import eva.bots.bot.RequestHandler;
import eva.bots.exception.TelegramRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MainMenuHandler {

    private final MainMenuButtonsHandler mainMenuButtonsHandler;
    private final RequestHandler requestHandler;
    private final String URGENT_REQUEST_USER = "usr_urgent_request_user";
    private final String REGULAR_REQUESTS_USER = "usr_regular_request_user";
    private final String HISTORY_OF_REQUESTS = "usr_history_of_requests";
    private final String OPEN = "usr_open_request_";
    private final String TEXTMESSAGE = "usr_textMessage_";
    private final String BACK = "usr_back_";

    public List<SendMessage> handleMainMenu(Update update) {

        if (update.getMessage() != null &&
                update.getMessage().getText().equals("/menu")){
            return mainMenuButtonsHandler.handleStartMessage(update.getMessage());
        }

        String data = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getFrom().getId();

        if (data.equals(HISTORY_OF_REQUESTS) || data.startsWith(BACK)) {
            return mainMenuButtonsHandler.provideRequestsInProcess(chatId);
        }
        if (data.equals(URGENT_REQUEST_USER)) {
            return Collections.singletonList(requestHandler.handleUrgentRequest(chatId));
        }
        if (data.equals(REGULAR_REQUESTS_USER)) {
            return Collections.singletonList(requestHandler.handleRegularRequest(chatId));
        }
        if (data.startsWith(OPEN)) {
            Long requestId = Long.parseLong(data.substring(OPEN.length()));
            return mainMenuButtonsHandler.handleOpenRequest(chatId, requestId);
        }
        if (data.startsWith(TEXTMESSAGE)) {
            Long requestId = Long.parseLong(data.substring(TEXTMESSAGE.length()));
            return mainMenuButtonsHandler.handleSendMessage(chatId, requestId);
        }

        throw new TelegramRuntimeException("Can't handle mein menu");
    }
}
