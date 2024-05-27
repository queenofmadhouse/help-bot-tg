package eva.bots.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class StartMessageHandler {

    public SendMessage handleStartMessage(Message message) {

        SendMessage sendMessage = new SendMessage();

        sendMessage.setText("Teeest message");
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(message.getChatId());

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRowsList = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();

        KeyboardButton keyboardButtonUrgent = new KeyboardButton();
        keyboardButtonUrgent.setText("Экстренный запрос");
        keyboardButtonUrgent.setRequestLocation(false);
        KeyboardButton keyboardButtonRegular = new KeyboardButton();
        keyboardButtonRegular.setText("Обычный запрос");
        keyboardButtonRegular.setRequestLocation(false);

        keyboardRow.add(keyboardButtonUrgent);
        keyboardRow.add(keyboardButtonRegular);

        keyboardRowsList.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowsList);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }
}
