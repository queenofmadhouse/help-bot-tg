package eva.bots;

import eva.bots.bot.TelegramBot;
import eva.bots.entity.RegularRequest;
import eva.bots.service.RegularRequestService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class ApplicationRunner {
    ;
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ApplicationRunner.class, args);

//        RegularRequestService regularRequestService = context.getBean(RegularRequestService.class);
//        regularRequestService.save(new RegularRequest());

        try {
            TelegramBot telegramBot = context.getBean(TelegramBot.class);

            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
