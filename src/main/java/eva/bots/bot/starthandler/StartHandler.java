package eva.bots.bot.starthandler;

import eva.bots.bot.mainmenu.MainMenuButtonsHandler;
import eva.bots.dto.TelegramMessageDTO;
import eva.bots.exception.TelegramRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartHandler {

    private final MainMenuButtonsHandler mainMenuButtonsHandler;

    public List<TelegramMessageDTO> handleMainMenu(Update update) {

        if (update.getMessage() != null &&
                update.getMessage().getText().equals("/start")){
            return provideStartMessage(update);
        }

        throw new TelegramRuntimeException("Can't handle start menu");
    }

    private List<TelegramMessageDTO> provideStartMessage(Update update) {

        Message message = update.getMessage();

        List<TelegramMessageDTO>  startMessages = new ArrayList<>();

        SendMessage messageWithText = new SendMessage();
        SendSticker sticker = new SendSticker();
        InputFile stickerId = new InputFile("CAACAgIAAxkBAAEF5HhmXwaA6meLFBlYKk5KhWMjHIMP4AACKBEAAtJt8Ejsb23DmtGplzUE");
        String text = "Привет! \uD83D\uDC4B \n" +
                "Я Мурзик с сайта Трансляции, теперь я могу проконсультировать вас и через этот бот! \n" +
                "\n" +
                "✔\uFE0FОбращайтесь ко мне за *информационной консультацией* по любым вопросам, с которыми работает Трансляция (" +
                "[медицинская помощь](https://translyaciya.com/doctors) / " +
                "[психологи](https://translyaciya.com/therapy) / " +
                "[сообщество](https://translyaciya.com/cities) / " +
                "[услуги других специалистов](https://translyaciya.com/others) / " +
                "[эмиграция](https://translyaciya.com/emigration) / " +
                "[группы поддержки](https://translyaciya.com/transhelp) / " +
                "[терапевтическая группа](https://translyaciya.com/group) / " +
                "[просвещение](https://translyaciya.com/materials) / " +
                "[юристы](https://translyaciya.com/lawyers) и др.) \n" +
                "\n" +
                "\uD83D\uDCCCЕсли вы попали в *экстренную ситуацию*, то нажмите кнопку \uD83C\uDD98 И я свяжусь с вами как можно скорее и передам вашу заявку специалистам. \n" +
                "\n" +
                "Экстренная ситуация - это положение, которое угрожает вашей жизни, здоровью или безопасности. (Например: нападение, донос, пришедшая повестка и др.) \n" +
                "\n" +
                "Надеюсь, что смогу вам помочь и быть полезным! \uD83D\uDC9C";

        messageWithText.setChatId(message.getChatId());
        messageWithText.setText(text);
        messageWithText.enableMarkdown(true);
        sticker.setChatId(message.getChatId());
        sticker.setSticker(stickerId);

        TelegramMessageDTO telegramMessageDTO = TelegramMessageDTO.builder()
                .sendMessage(messageWithText)
                .sendSticker(sticker)
                .build();

        startMessages.add(telegramMessageDTO);

        mainMenuButtonsHandler.handleStartMessage(message).forEach(menu -> startMessages.add(TelegramMessageDTO.builder()
                .sendMessage(menu)
                .build()));

        return startMessages;
    }
}
