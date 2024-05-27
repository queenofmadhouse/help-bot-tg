package eva.bots.bot.adminpanel;

import eva.bots.exception.TelegramRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.event.spi.CallbackRegistryConsumer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminPanelProvider {

    private final Long adminChatId = 225773842L; // FIXME
    private final String URGENT_REQUEST = "urgent_request";
    private final String REGULAR_REQUESTS = "regular_requests";
    private final String REQUESTS_IN_PROCESS = "requests_in_process";

    public SendMessage provideAdminPanel(Update update) {

        authoriseUserAndProvideAdminPanel(update);

        return authoriseUserAndProvideAdminPanel(update);
    }

    private SendMessage authoriseUserAndProvideAdminPanel(Update update) {

        Message message = update.getMessage();

        if (message.getChatId().equals(adminChatId)) {
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .text("У вас нет доступа к использованию данной команды!")
                    .build();
        }

        if (!update.hasCallbackQuery()) {
            return provideAdminPanelButtons(message);
        } else {
            return handleButtons(update);
        }
    }

    private SendMessage handleButtons(Update update) {

        Message message = update.getMessage();

        if (update.getCallbackQuery().getData().equals(URGENT_REQUEST)) {
            return provideUrgentRequests(message);
        }

        if (update.getCallbackQuery().getData().equals(REGULAR_REQUESTS)) {
            return provideRegularRequests(message);
        }

        if (update.getCallbackQuery().getData().equals(REQUESTS_IN_PROCESS)) {
            return provideRequestsInProcess(message);
        }

        throw new TelegramRuntimeException("Bad CallBackQuery, can't handle buttons");
    }

    private SendMessage provideUrgentRequests(Message message) {
        return null; // TODO: add logic of creating list of requests
    }

    private SendMessage provideRegularRequests(Message message) {
        return null; // TODO: add logic of creating list of requests
    }

    private SendMessage provideRequestsInProcess(Message message) {
        return null; // TODO: add logic of creating list of requests
    }

    private SendMessage provideAdminPanelButtons(Message message) {

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Добро пожаловать в админ панель");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardRows = new ArrayList<>();

        // Urgent requests button
        List<InlineKeyboardButton> reportSaleRow = new ArrayList<>();
        InlineKeyboardButton saleBtn = new InlineKeyboardButton();
        saleBtn.setCallbackData(URGENT_REQUEST);
        reportSaleRow.add(saleBtn);
        inlineKeyboardRows.add(reportSaleRow);

        // Regular requests button
        List<InlineKeyboardButton> reportBuyRow = new ArrayList<>();
        InlineKeyboardButton buyBtn = new InlineKeyboardButton("Отчет по покупкам");
        buyBtn.setCallbackData(REGULAR_REQUESTS);
        reportBuyRow.add(buyBtn);
        inlineKeyboardRows.add(reportBuyRow);

        // Requests in process button
        List<InlineKeyboardButton> setPriceRow = new ArrayList<>();
        InlineKeyboardButton priceBtn = new InlineKeyboardButton("Установить новую цену");
        priceBtn.setCallbackData(REQUESTS_IN_PROCESS);
        setPriceRow.add(priceBtn);
        inlineKeyboardRows.add(setPriceRow);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboardRows);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }
}
