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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${app.constants.bot.bot-name}")
    private String botName;
    @Value("${app.constants.bot.bot-token}")
    private String botAPI;
    private final MessagesHandler messagesHandler;
    private final Long adminChatId = 225773842L;

    @Override
    public void onUpdateReceived(Update update) {

        log.info("Получен апдейт: {}", update);

        executeMessage(messagesHandler.handleMessages(update));
    }

    @EventListener
    public void handleNotificationEvent(SendMessage event) {
        executeMessage(event);
    }

    private void executeMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new TelegramRuntimeException("Ошибка при обработке отправки сообщения: {}", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @Override
    public String getBotToken() {

        return botAPI;
    }
}
