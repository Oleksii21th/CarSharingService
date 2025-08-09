package carsharing.carsharingservice.repository;

import carsharing.carsharingservice.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
}

