package carsharing.carsharingservice.service;

import carsharing.carsharingservice.model.Rental;
import java.util.List;

public interface RentalService {
    Rental save(Rental rental);

    List<Rental> findRentalsByUser(Long userId, Boolean isActive);

    Rental findRentalById(Long id);

    Rental returnRental(Long id, Rental rental);
}
