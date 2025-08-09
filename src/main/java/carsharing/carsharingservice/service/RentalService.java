package carsharing.carsharingservice.service;

import carsharing.carsharingservice.model.Rental;
import java.util.List;
import java.util.Optional;

public interface RentalService {
    Rental save(Rental rental);

    List<Rental> findRentalsByUser(Long userId, Boolean isActive);

    Optional<Rental> findRental(Long id);

    Rental returnRental(Long id, Rental rental);
}
