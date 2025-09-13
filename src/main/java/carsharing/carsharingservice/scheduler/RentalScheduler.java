package carsharing.carsharingservice.scheduler;

import carsharing.carsharingservice.model.Rental;
import carsharing.carsharingservice.repository.RentalRepository;
import carsharing.carsharingservice.service.TelegramNotificationService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RentalScheduler {

    private final RentalRepository rentalRepository;
    private final TelegramNotificationService telegramNotificationService;

    public RentalScheduler(RentalRepository rentalRepository,
                           TelegramNotificationService telegramNotificationService) {
        this.rentalRepository = rentalRepository;
        this.telegramNotificationService = telegramNotificationService;
    }

    @Scheduled(cron = "0 28 17 * * *")
    public void checkOverdueRentals() {
        LocalDate today = LocalDate.now();

        List<Rental> overdueRentals = rentalRepository
                .findByReturnDateBeforeAndActualReturnDateIsNull(today);

        if (overdueRentals.isEmpty()) {
            telegramNotificationService.sendNoOverduesNotification();
            return;
        }

        for (Rental rental : overdueRentals) {
            telegramNotificationService.sendOverdueRentalNotification(rental);
        }
    }
}