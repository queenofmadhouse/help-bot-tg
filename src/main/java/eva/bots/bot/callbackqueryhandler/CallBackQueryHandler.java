package eva.bots.bot.callbackqueryhandler;

import eva.bots.bot.adminpanel.AdminPanelProvider;
import eva.bots.bot.mainmenu.MainMenuHandler;
import eva.bots.exception.TelegramRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CallBackQueryHandler {

    private final String ADM = "adm_";
    private final String USR = "usr_";

    private final AdminPanelProvider adminPanelProvider;
    private final MainMenuHandler mainMenuHandler;

    public List<SendMessage> handleCallBackQuery(Update update) {

        String data = update.getCallbackQuery().getData();
        System.out.println("Data is: " + data);

        if (data.startsWith(ADM)) {
            System.out.println("CallBackQuery from admin");
            return adminPanelProvider.provideAdminPanel(update);
        }
        if (data.startsWith(USR)) {
            System.out.println("CallBackQuery from user");
            return mainMenuHandler.handleMainMenu(update);
        }

        throw new TelegramRuntimeException("Can't handle callBackQuery in update {}" + update);
    }
}
