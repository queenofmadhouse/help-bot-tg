package eva.bots.bot.adminpanel;

import eva.bots.exception.TelegramRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminPanelProvider {
    private final String URGENT_REQUEST = "adm_urgent_request";
    private final String REGULAR_REQUESTS = "adm_regular_requests";
    private final String REQUESTS_IN_PROCESS = "adm_requests_in_process";
    private final String ACCEPT = "adm_accept_";
    private final String REJECT = "adm_reject_";
    private final String START = "adm_start_";
    private final String ARCHIVE = "adm_archive_";
    private final String TEXTMESSAGE = "adm_textMessage_";
    private final String BACK = "amd_back_";
    private final ApplicationEventPublisher eventPublisher;
    private final AdminButtonsHandler adminButtonsHandler;


    public List<SendMessage> provideAdminPanel(Update update) {

        authoriseUserAndProvideAdminPanel(update);

        return authoriseUserAndProvideAdminPanel(update);
    }

    private List<SendMessage> authoriseUserAndProvideAdminPanel(Update update) {

        if (update.hasCallbackQuery()) {
            return handleButtons(update);
        }

        if (!update.hasCallbackQuery()) {
            return adminButtonsHandler.provideAdminPanelButtons(update);
        }

        throw new TelegramRuntimeException("Can't handle admin panel");
    }

    private List<SendMessage> handleButtons(Update update) {

        String data = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getFrom().getId();

        if (data.equals(URGENT_REQUEST)) {

            return adminButtonsHandler.provideRequests(chatId, true);
        }

        if (data.equals(REGULAR_REQUESTS)) {
            return adminButtonsHandler.provideRequests(chatId, false);
        }

        if (data.equals(REQUESTS_IN_PROCESS)) {
            return adminButtonsHandler.provideRequestsInProcess(chatId);
        }

        if (data.startsWith(ACCEPT)) {
            Long requestId = Long.parseLong(data.substring(ACCEPT.length()));
            return adminButtonsHandler.handleAcceptRequest(chatId, requestId);
        } else if (data.startsWith(REJECT)) {
            Long requestId = Long.parseLong(data.substring(REJECT.length()));
            return adminButtonsHandler.handleRejectRequest(chatId, requestId);
        }

        if (data.startsWith(START)) {
            Long requestId = Long.parseLong(data.substring(START.length()));
            return adminButtonsHandler.handleStartRequest(chatId, requestId);
        } else if (data.startsWith(ARCHIVE)) {
            Long requestId = Long.parseLong(data.substring(ARCHIVE.length()));
            return adminButtonsHandler.handleRejectRequest(chatId, requestId);
        }

        if (data.startsWith(TEXTMESSAGE)) {
            Long requestId = Long.parseLong(data.substring(TEXTMESSAGE.length()));
            return adminButtonsHandler.handleSendMessage(chatId, requestId);
        } else if (data.startsWith(BACK)) {
            return adminButtonsHandler.provideRequestsInProcess(chatId);
        }

        throw new TelegramRuntimeException("Bad CallBackQuery, can't handle buttons");
    }
}
