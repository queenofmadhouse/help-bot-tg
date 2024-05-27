package eva.bots.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eva.bots.bot.adminpanel.AdminPanelProvider;
import eva.bots.dto.WebAppDataDTO;
import eva.bots.entity.RegularRequest;
import eva.bots.entity.UrgentRequest;
import eva.bots.exception.TelegramRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagesHandler {

    private final StartMessageHandler startMessageHandler;
    private final RequestHandler requestHandler;
    private final WebAppHandler webAppHandler;
    private final AdminPanelProvider adminPanelProvider;

    public SendMessage handleMessages(Update update) {

        if (update == null) {
            throw new TelegramRuntimeException("Update is null");
        }

        Message message = update.getMessage();

        if (message.hasText()) {
            String text = message.getText();

            if (text.equals("/start")) {

                return startMessageHandler.handleStartMessage(message);
            }

            if (text.equals("Экстренный запрос")) {
                log.info("Trying to handel urgent request");

                return requestHandler.handleUrgentRequest(message);
            }

            if (text.equals("Обычный запрос")) {
                log.info("Trying to handel regular request");

                return requestHandler.handleRegularRequest(message);
            }

            if (text.equals("/admin") || update.hasCallbackQuery()) {
                return adminPanelProvider.provideAdminPanel(update);
            }
        }

        if (update.hasMessage() && message.getWebAppData() != null) {
            log.info("Trying to handel webApp data");

            return webAppHandler.handleWebApp(message);
        }

        throw new TelegramRuntimeException("Can't handle message");
    }
}
