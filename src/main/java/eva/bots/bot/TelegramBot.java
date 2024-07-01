package eva.bots.bot;

import eva.bots.dto.TelegramMessageDTO;
import eva.bots.exception.TelegramRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void onUpdateReceived(Update update) {

        List<TelegramMessageDTO> list = messagesHandler.handleMessages(update);
        log.info(list.toString());

        for (TelegramMessageDTO message : list) {
            log.info(message.toString());
            executeMessageWithRetry(message);
        }
    }

    @EventListener
    public void sendNotification(SendMessage event) {
        log.info("Received event in sendNotification: {}", event); // Добавляем логирование
        executeMessageWithRetry(event);
    }

    private void executeMessageWithRetry(TelegramMessageDTO message) {

        if (message.getSendMessage() != null) {
            sendApiMethodAsync(message.getSendMessage());
        }

        if (message.getSendSticker() != null) {
            try {
                execute(message.getSendSticker());
            } catch (TelegramApiException e) {
                throw new TelegramRuntimeException("Can't send sticker: ", e);
            }
        }
    }
    private void executeMessageWithRetry(SendMessage message) {

        sendApiMethodAsync(message);
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
