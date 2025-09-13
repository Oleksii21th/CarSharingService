package carsharing.carsharingservice.service;

import carsharing.carsharingservice.model.Rental;

public interface TelegramNotificationService {
    void sendRentalCreatedNotification(Rental rental);
    void sendOverdueRentalNotification(Rental rental);
    void sendNoOverduesNotification();
}
