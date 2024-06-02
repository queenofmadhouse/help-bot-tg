package eva.bots.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestHandler {

    @Value("${app.constants.bot.web-url}")
    private String url;
    private final Jedis jedis;

    public SendMessage handleUrgentRequest(Long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);

        KeyboardButton keyboardButton = new KeyboardButton("Отправить запрос");
        keyboardButton.setWebApp(new WebAppInfo(url));

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(keyboardButton);

        List<KeyboardRow> keyboardRowsList = new ArrayList<>();
        keyboardRowsList.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRowsList);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Пожалуйста нажмите на кнопку ниже что бы отправить запрос");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        jedis.set(chatId.toString(), "urgent");

        return sendMessage;
    }

    public SendMessage handleRegularRequest(Long chatId) {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);

        KeyboardButton keyboardButton = new KeyboardButton("Отправить запрос");
        keyboardButton.setWebApp(new WebAppInfo(url));

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(keyboardButton);

        List<KeyboardRow> keyboardRowsList = new ArrayList<>();
        keyboardRowsList.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRowsList);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Пожалуйста нажмите на кнопку ниже что бы отправить запрос");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        jedis.set(chatId.toString(), "regular");
        return sendMessage;
    }
}
