package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.config.TelegramProperties;
import carsharing.carsharingservice.model.Payment;
import carsharing.carsharingservice.model.Rental;
import carsharing.carsharingservice.service.TelegramNotificationService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramNotificationServiceImpl implements TelegramNotificationService {
    private final TelegramProperties telegramProperties;

    public TelegramNotificationServiceImpl(TelegramProperties telegramProperties) {
        this.telegramProperties = telegramProperties;
    }

    @Override
    public void sendRentalCreatedNotification(Rental rental) {
        String message = String.format(
                "New rental created!\n\n"
                        + "User: %s %s (%s)\n"
                        + "Car: %s %s (%s)\n"
                        + "Rental date: %s\n"
                        + "Return date: %s",
                rental.getUser().getFirstName(),
                rental.getUser().getLastName(),
                rental.getUser().getUsername(),
                rental.getCar().getBrand(),
                rental.getCar().getModel(),
                rental.getCar().getType(),
                rental.getRentalDate(),
                rental.getReturnDate()
        );

        sendMessage(message);
    }

    @Override
    public void sendOverdueRentalNotification(Rental rental) {
        String message = String.format(
                "*Overdue rental!*\n\n"
                        + "User: %s %s (%s)\n"
                        + "Car: %s %s (%s)\n"
                        + "Return date: %s\n"
                        + "Current date: %s",
                rental.getUser().getFirstName(),
                rental.getUser().getLastName(),
                rental.getUser().getUsername(),
                rental.getCar().getBrand(),
                rental.getCar().getModel(),
                rental.getCar().getType(),
                rental.getReturnDate(),
                java.time.LocalDate.now()
        );

        sendMessage(message);
    }

    @Override
    public void sendPaymentSuccessNotification(Payment payment) {
        String message = String.format(
                "Payment completed successfully!\n\n"
                        + "User: %s %s (%s)\n"
                        + "Amount: %s\n"
                        + "Rental ID: %d\n"
                        + "Car: %s %s",
                payment.getRental().getUser().getFirstName(),
                payment.getRental().getUser().getLastName(),
                payment.getRental().getUser().getUsername(),
                payment.getAmountToPay(),
                payment.getRental().getId(),
                payment.getRental().getCar().getBrand(),
                payment.getRental().getCar().getModel()
        );

        sendMessage(message);
    }

    @Override
    public void sendNoOverduesNotification() {
        String message = "No overdue rentals today!";
        sendMessage(message);
    }

    private void sendMessage(String text) {
        String url = telegramProperties.getSendMessageUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String safeText = escapeText(text);
        String body = String.format("""
                {
                  "chat_id": "%s",
                  "text": "%s",
                  "parse_mode": "Markdown"
                }
                """, telegramProperties.getChatId(), safeText);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(url, request, String.class);
    }

    private String escapeText(String text) {
        if (text == null) {
            return "";
        }

        return text.replace("\"", "\\\"").replace("\n", "\\n");
    }
}
