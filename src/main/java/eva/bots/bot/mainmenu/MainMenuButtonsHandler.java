package eva.bots.bot.mainmenu;

import eva.bots.dto.TelegramMessageDTO;
import eva.bots.entity.Message;
import eva.bots.entity.Request;
import eva.bots.service.MessageService;
import eva.bots.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MainMenuButtonsHandler {

    private final String URGENT_REQUEST_USER = "usr_urgent_request_user";
    private final String REGULAR_REQUESTS_USER = "usr_regular_request_user";
    private final String HISTORY_OF_REQUESTS = "usr_history_of_requests";
    private final String OPEN = "usr_open_request_";
    private final String TEXTMESSAGE = "usr_textMessage_";
    private final String BACK = "usr_back_";
    private final RequestService requestService;
    private final MessageService messageService;
    private final Jedis jedis;
    private final ApplicationEventPublisher eventPublisher;

    public List<SendMessage> handleStartMessage(org.telegram.telegrambots.meta.api.objects.Message message) {

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Добро пожаловать в главное меню");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardRows = new ArrayList<>();

        // Requests in process button if user have one or more requests
        if (!requestService.findAllByRelatedUserId(message.getChatId()).isEmpty()) {
            List<InlineKeyboardButton> historyOfRequestsRow = new ArrayList<>();
            InlineKeyboardButton historyOfRequestsBtn = new InlineKeyboardButton();
            historyOfRequestsBtn.setText("Мои запросы");
            historyOfRequestsBtn.setCallbackData(HISTORY_OF_REQUESTS);
            historyOfRequestsRow.add(historyOfRequestsBtn);
            inlineKeyboardRows.add(historyOfRequestsRow);
        }

        // Regular requests button
        List<InlineKeyboardButton> regularRequestRow = new ArrayList<>();
        InlineKeyboardButton regularRequestBtn = new InlineKeyboardButton();
        regularRequestBtn.setText("Информационная консультация");
        regularRequestBtn.setCallbackData(REGULAR_REQUESTS_USER);
        regularRequestRow.add(regularRequestBtn);
        inlineKeyboardRows.add(regularRequestRow);

        // Urgent requests button
        List<InlineKeyboardButton> urgentRequestRow = new ArrayList<>();
        InlineKeyboardButton urgentRequestBtn = new InlineKeyboardButton();
        urgentRequestBtn.setText("Экстренный запрос \uD83C\uDD98");
        urgentRequestBtn.setCallbackData(URGENT_REQUEST_USER);
        urgentRequestRow.add(urgentRequestBtn);
        inlineKeyboardRows.add(urgentRequestRow);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboardRows);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.enableMarkdown(true);
        return Collections.singletonList(sendMessage);
    }

    public List<SendMessage> provideRequestsInProcess(Long chatId) {

        List<Request> requests = requestService.findAllByRelatedUserId(chatId);

        if (requests.isEmpty()) {
            return List.of(SendMessage.builder()
                    .chatId(chatId)
                    .text("Вы еще не оставляли запросы")
                    .build());
        }

        List<SendMessage> messages = new ArrayList<>();

        messages.add(SendMessage.builder()
                .chatId(chatId)
                .text("Ваши запросы:")
                .build());

        for (Request request : requests) {
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

            List<InlineKeyboardButton> row = new ArrayList<>();

            InlineKeyboardButton textMessageButton = new InlineKeyboardButton("Открыть");
            textMessageButton.setCallbackData(OPEN + request.getId());
            row.add(textMessageButton);

            keyboardRows.add(row);

            inlineKeyboardMarkup.setKeyboard(keyboardRows);

            String title = "*Запрос:*\n";

            if (request.isUrgent()) {
                title = "*Срочный запрос:*\n";
            }

            String requestText = title +
                    "**id запроса: " + request.getId() + "\n" +
                    "**Запрос:** " + request.getRequestText() + "\n";

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(requestText);

            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            sendMessage.setParseMode(ParseMode.MARKDOWN);

            messages.add(sendMessage);
        }

        return messages;
    }

    public List<SendMessage> handleSendMessage(Long chatId, Long requestId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Напишите ваше сообщение: ");

        jedis.set(chatId.toString() + ":state", "usr_waiting_for_message");
        jedis.set(chatId.toString() + ":requestId", requestId.toString());

        return Collections.singletonList(sendMessage);
    }

    public List<SendMessage> handleOpenRequest(Long chatId, Long requestId) {

        List<eva.bots.entity.Message> messagesRelatedToRequest = messageService.findAllMessagesRelatedToRequest(requestId);
        Request request = requestService.findById(requestId);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        if (!request.isInTheArchive()) {
            InlineKeyboardButton acceptButton = new InlineKeyboardButton("Написать");
            acceptButton.setCallbackData(TEXTMESSAGE + request.getId());
            row.add(acceptButton);
        }

        InlineKeyboardButton rejectButton = new InlineKeyboardButton("Назад");
        rejectButton.setCallbackData(BACK + request.getId());
        row.add(rejectButton);

        keyboardRows.add(row);

        inlineKeyboardMarkup.setKeyboard(keyboardRows);

        String title = "*Запрос:*\n";

        if (request.isUrgent()) {
            title = "*Срочный запрос:*\n";
        }

        String requestText = title +
                "**id: " + request.getId() + "\n" +
                "**Имя:** " + request.getUserName() + "\n" +
                "**Местоимения:** " + request.getUserPronouns() + "\n" +
                "**Запрос:** " + request.getRequestText() + "\n\n";

        if (!messagesRelatedToRequest.isEmpty()) {
            requestText += "**История сообщений:**" + "\n";
            for (eva.bots.entity.Message message : messagesRelatedToRequest) {

                if (message.isFromAdmin()) {
                    requestText += "**Консультант:** ";
                } else {
                    requestText += "**Пользователь:** ";
                }

                requestText += message.getMessageText() + "\n";
            }
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(requestText);

        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        return Collections.singletonList(sendMessage);
    }

    public List<SendMessage> sendPrivateMessage(Long requestId, String text) {
        Request request = requestService.findById(requestId);

        Message message = Message.builder()
                .request(request)
                .userChatId(request.getTgChatId())
                .adminChatId(request.getRelatedAdminId())
                .fromAdmin(false)
                .messageText(text)
                .build();
        messageService.save(message);

        List<SendMessage> returnMessage = Arrays.asList(
                new SendMessage(request.getTgChatId().toString(), "Cообщение отправлено "),
                provideRequestsInProcess(request.getTgChatId()).getLast()
        );

        String requestType;

        if (request.isUrgent()) {
            requestType = "срочному";
        } else {
            requestType = "обычному";
        }

        SendMessage event = new SendMessage();
        event.setChatId(request.getRelatedAdminId());
        event.setText("Пришло новое сообщение по " + requestType + " запросу" + "\n" +
                "ID запроса: " + request.getId());
        eventPublisher.publishEvent(TelegramMessageDTO.builder()
                .sendMessage(event)
                .build());

        return returnMessage;
    }
}
