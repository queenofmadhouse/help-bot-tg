package eva.bots.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

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

        List<SendMessage> list = messagesHandler.handleMessages(update);

        for (SendMessage message : list) {
            executeMessageWithRetry(message);
        }
    }

    @EventListener
    public void sendNotification(SendMessage event) {
        System.out.println("sendNotification");
        executeMessageWithRetry(event);
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
