package eva.bots.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;

@Builder
@Getter
@Setter
public class TelegramMessageDTO {
    private SendMessage sendMessage;
    private SendSticker sendSticker;
}
