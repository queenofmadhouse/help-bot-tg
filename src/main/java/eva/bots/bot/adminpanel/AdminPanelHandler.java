package eva.bots.bot.adminpanel;

import eva.bots.exception.TelegramRuntimeException;
import eva.bots.service.AdminService;
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
public class AdminPanelHandler {
    private final String URGENT_REQUEST = "adm_urgent_request";
    private final String REGULAR_REQUESTS = "adm_regular_requests";
    private final String REQUESTS_IN_PROCESS = "adm_requests_in_process";
    private final String ACCEPT = "adm_accept_";
    private final String REJECT = "adm_reject_";
    private final String START = "adm_start_";
    private final String ARCHIVE = "adm_archive_";
    private final String TEXTMESSAGE = "adm_textMessage_";
    private final String BACK = "amd_back_";
    private final AdminPanelButtonsHandler adminPanelButtonsHandler;
    private final AdminService adminService;


    public List<SendMessage> provideAdminPanel(Update update) {

        Long userId = update.hasCallbackQuery() ? update.getCallbackQuery().getFrom().getId() : update.getMessage().getChatId();

        if(!authoriseUser(userId)) {

            return Collections.singletonList(SendMessage.builder()
                    .chatId(userId)
                    .text("Вы не зарегистрированы в качестве администратора")
                    .build());
        } else {
            if (update.hasCallbackQuery()) {
                return handleButtons(update);
            }
            if (update.hasMessage()) {
                return adminPanelButtonsHandler.provideAdminPanelButtons(update);
            }
        }

        throw new TelegramRuntimeException("Can't handle admin panel");

    }

    private boolean authoriseUser(Long id) {

        return adminService.existByTelegramUserId(id);
    }

    private List<SendMessage> handleButtons(Update update) {

        String data = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getFrom().getId();

        if (data.equals(URGENT_REQUEST)) {

            return adminPanelButtonsHandler.provideRequests(chatId, true);
        }

        if (data.equals(REGULAR_REQUESTS)) {
            return adminPanelButtonsHandler.provideRequests(chatId, false);
        }

        if (data.equals(REQUESTS_IN_PROCESS)) {
            return adminPanelButtonsHandler.provideRequestsInProcess(chatId);
        }

        if (data.startsWith(ACCEPT)) {
            Long requestId = Long.parseLong(data.substring(ACCEPT.length()));
            return adminPanelButtonsHandler.handleAcceptRequest(chatId, requestId);
        } else if (data.startsWith(REJECT)) {
            Long requestId = Long.parseLong(data.substring(REJECT.length()));
            return adminPanelButtonsHandler.handleRejectRequest(chatId, requestId);
        }

        if (data.startsWith(START)) {
            Long requestId = Long.parseLong(data.substring(START.length()));
            return adminPanelButtonsHandler.handleStartRequest(chatId, requestId);
        } else if (data.startsWith(ARCHIVE)) {
            Long requestId = Long.parseLong(data.substring(ARCHIVE.length()));
            return adminPanelButtonsHandler.handleRejectRequest(chatId, requestId);
        }

        if (data.startsWith(TEXTMESSAGE)) {
            Long requestId = Long.parseLong(data.substring(TEXTMESSAGE.length()));
            return adminPanelButtonsHandler.handleSendMessage(chatId, requestId);
        } else if (data.startsWith(BACK)) {
            return adminPanelButtonsHandler.provideRequestsInProcess(chatId);
        }

        throw new TelegramRuntimeException("Bad CallBackQuery, can't handle buttons");
    }
}
