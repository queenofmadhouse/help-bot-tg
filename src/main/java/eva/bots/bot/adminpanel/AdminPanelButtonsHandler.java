package eva.bots.bot.adminpanel;

import eva.bots.bot.mainmenu.MainMenuButtonsHandler;
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
import org.telegram.telegrambots.meta.api.objects.Update;
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
public class AdminPanelButtonsHandler {

    private final String URGENT_REQUEST = "adm_urgent_request";
    private final String REGULAR_REQUESTS = "adm_regular_requests";
    private final String REQUESTS_IN_PROCESS = "adm_requests_in_process";
    private final String ACCEPT = "adm_accept_";
    private final String REJECT = "adm_reject_";
    private final String START = "adm_start_";
    private final String ARCHIVE = "adm_archive_";
    private final String TEXTMESSAGE = "adm_textMessage_";
    private final String BACK = "amd_back_";
    private final RequestService requestService;
    private final MessageService messageService;
    private final MainMenuButtonsHandler mainMenuButtonsHandler;
    private final ApplicationEventPublisher eventPublisher;
    private final Jedis jedis;

    public List<SendMessage> provideRequests(Long chatId, boolean isUrgent) {
        List<Request> requests;
        String text;

        if (isUrgent) {
            requests = requestService.findAllUrgent();
            text = "Срочных запросов нет";
        } else {
            requests = requestService.findAllRegular();
            text = "Обычных запросов нет";
        }

        if (requests.isEmpty()) {

            return List.of(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build());
        }

        List<SendMessage> messages = new ArrayList<>();

        messages.add(SendMessage.builder()
                .chatId(chatId)
                .text("Запросы:")
                .build());

        for (Request request : requests) {
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

            List<InlineKeyboardButton> row = new ArrayList<>();

            InlineKeyboardButton acceptButton = new InlineKeyboardButton("Принять");
            acceptButton.setCallbackData(ACCEPT + request.getId());
            row.add(acceptButton);

            InlineKeyboardButton rejectButton = new InlineKeyboardButton("Отклонить");
            rejectButton.setCallbackData(REJECT + request.getId());
            row.add(rejectButton);

            keyboardRows.add(row);

            inlineKeyboardMarkup.setKeyboard(keyboardRows);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(
                    "*Запрос:*\n" +
                            "**id: " + request.getId() + "\n" +
                            "**Имя:** " + escapeMarkdown(request.getUserName()) + "\n" +
                            "**Местоимения:** " + escapeMarkdown(request.getUserPronouns()) + "\n" +
                            "**Запрос:** " + escapeMarkdown(request.getRequestText()) + "\n\n");

            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            sendMessage.setParseMode(ParseMode.MARKDOWN);

            messages.add(sendMessage);
        }

        return messages;
    }

    public List<SendMessage> provideRequestsInProcess(Long chatId) {

        List<Request> requests = requestService.findAllByRelatedAdminId(chatId);

        if (requests.isEmpty()) {
            return List.of(SendMessage.builder()
                    .chatId(chatId)
                    .text("Запросов в обработке нет")
                    .build());
        }

        List<SendMessage> messages = new ArrayList<>();

        messages.add(SendMessage.builder()
                .chatId(chatId)
                .text("Запросы:")
                .build());

        for (Request request : requests) {
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

            List<InlineKeyboardButton> row = new ArrayList<>();

            InlineKeyboardButton textMessageButton = new InlineKeyboardButton("Начать");
            textMessageButton.setCallbackData(START + request.getId());
            row.add(textMessageButton);

            InlineKeyboardButton archiveRequestButton = new InlineKeyboardButton("Выполнено");
            archiveRequestButton.setCallbackData(ARCHIVE + request.getId());
            row.add(archiveRequestButton);

            keyboardRows.add(row);

            inlineKeyboardMarkup.setKeyboard(keyboardRows);

            String title = "*Запрос:*\n";

            if (request.isUrgent()) {
                title = "*Срочный запрос:*\n";
            }

            String requestText = title +
                    "**id: " + request.getId() + "\n" +
                    "**Имя:** " + escapeMarkdown(request.getUserName()) + "\n" +
                    "**Местоимения:** " + escapeMarkdown(request.getUserPronouns()) + "\n" +
                    "**Запрос:** " + escapeMarkdown(request.getRequestText()) + "\n";

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(requestText);

            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            sendMessage.setParseMode(ParseMode.MARKDOWN);

            messages.add(sendMessage);
        }

        return messages;
    }

    public List<SendMessage> handleStartRequest(Long chatId, Long requestId) {

        List<Message> messagesRelatedToRequest = messageService.findAllMessagesRelatedToRequest(requestId);
        Request request = requestService.findById(requestId);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton acceptButton = new InlineKeyboardButton("Написать");
        acceptButton.setCallbackData(TEXTMESSAGE + request.getId());
        row.add(acceptButton);

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
                "**Имя:** " + escapeMarkdown(request.getUserName()) + "\n" +
                "**Местоимения:** " + escapeMarkdown(request.getUserPronouns()) + "\n" +
                "**Запрос:** " + escapeMarkdown(request.getRequestText()) + "\n\n";

        if (!messagesRelatedToRequest.isEmpty()) {
            requestText += "**История сообщений:**" + "\n";
            for (Message message : messagesRelatedToRequest) {

                if (message.isFromAdmin()) {
                    requestText += "**Консультант:** ";
                } else {
                    requestText += "**Пользователь:** ";
                }

                requestText += escapeMarkdown(message.getMessageText()) + "\n";
            }
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(requestText);

        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        return Collections.singletonList(sendMessage);
    }

    public List<SendMessage> handleSendMessage(Long chatId, Long requestId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Напишите ваше сообщение: ");

        jedis.set(chatId.toString() + ":state", "adm_waiting_for_message");
        jedis.set(chatId.toString() + ":requestId", requestId.toString());
        return Collections.singletonList(sendMessage);
    }


    public List<SendMessage> handleAcceptRequest(Long chatId, Long requestId) {

        Request request = requestService.findById(requestId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Запрос успешно принят в обработку");

        request.setRelatedAdminId(chatId);
        request.setInWork(true);
        requestService.save(request);

        SendMessage notification = new SendMessage();
        notification.setChatId(request.getTgChatId());
        notification.setText("Ваш запрос обрабатывается");

        eventPublisher.publishEvent(notification);

        return Collections.singletonList(sendMessage);
    }

    public List<SendMessage> handleRejectRequest(Long chatId, Long requestId) {

        Request request = requestService.findById(requestId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Запрос успешно удален в архив");

        request.setInTheArchive(true);
        requestService.save(request);

        SendMessage notification = new SendMessage();
        notification.setChatId(request.getTgChatId());
        notification.setText("Ваш запрос отклонен");

        eventPublisher.publishEvent(notification);

        return Collections.singletonList(sendMessage);
    }

    public List<SendMessage> sendPrivateMessage(Long requestId, String text) {
        Request request = requestService.findById(requestId);

        Message message = Message.builder()
                .request(request)
                .userChatId(request.getTgChatId())
                .adminChatId(request.getRelatedAdminId())
                .fromAdmin(true)
                .messageText(text)
                .build();
        messageService.save(message);

        List<SendMessage> returnMessage = Arrays.asList(
                new SendMessage(request.getRelatedAdminId().toString(), "Cообщение отправлено "),
                provideRequestsInProcess(request.getRelatedAdminId()).getLast()
        );

        SendMessage event = new SendMessage();
        event.setChatId(request.getTgChatId());
        event.setText("Пришло новое сообщение");

        eventPublisher.publishEvent(event);

        event = mainMenuButtonsHandler.handleOpenRequest(request.getTgChatId(), request.getId()).getLast();

        eventPublisher.publishEvent(event);
        return returnMessage;
    }

    public List<SendMessage> provideAdminPanelButtons(Update update) {

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Добро пожаловать в админ панель");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboardRows = new ArrayList<>();

        // Urgent requests button
        List<InlineKeyboardButton> urgentRequestRow = new ArrayList<>();
        InlineKeyboardButton urgentRequestBtn = new InlineKeyboardButton();
        urgentRequestBtn.setText("Срочные запросы");
        urgentRequestBtn.setCallbackData(URGENT_REQUEST);
        urgentRequestRow.add(urgentRequestBtn);
        inlineKeyboardRows.add(urgentRequestRow);

        // Regular requests button
        List<InlineKeyboardButton> regularRequestRow = new ArrayList<>();
        InlineKeyboardButton regularRequestBtn = new InlineKeyboardButton();
        regularRequestBtn.setText("Обычные запросы");
        regularRequestBtn.setCallbackData(REGULAR_REQUESTS);
        regularRequestRow.add(regularRequestBtn);
        inlineKeyboardRows.add(regularRequestRow);

        // Requests in process button
        List<InlineKeyboardButton> requestsInProcessRow = new ArrayList<>();
        InlineKeyboardButton requestsInProcessBtn = new InlineKeyboardButton();
        requestsInProcessBtn.setText("Запросы в обработке");
        requestsInProcessBtn.setCallbackData(REQUESTS_IN_PROCESS);
        requestsInProcessRow.add(requestsInProcessBtn);
        inlineKeyboardRows.add(requestsInProcessRow);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboardRows);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return Collections.singletonList(sendMessage);
    }

    private String escapeMarkdown(String text) {
        return text.replaceAll("([*_\\[\\]()~`])", "\\\\$1");
    }
}
