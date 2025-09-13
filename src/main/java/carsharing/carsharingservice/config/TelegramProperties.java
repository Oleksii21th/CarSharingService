package carsharing.carsharingservice.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

@Component
public class TelegramProperties {
    private static final String TELEGRAM_BOT_TOKEN =
            Dotenv.configure().load().get("TELEGRAM_BOT_TOKEN");
    private static final String TELEGRAM_CHAT_ID =
            Dotenv.configure().load().get("TELEGRAM_CHAT_ID");
    private static final String TELEGRAM_API_BASE = "https://api.telegram.org/bot";

    public String getBotToken() {
        return TELEGRAM_BOT_TOKEN;
    }

    public String getChatId() {
        return TELEGRAM_CHAT_ID;
    }

    public String getSendMessageUrl() {
        return TELEGRAM_API_BASE + TELEGRAM_BOT_TOKEN + "/sendMessage";
    }
}
