package carsharing.carsharingservice.repository;

import carsharing.carsharingservice.model.Rental;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByUserIdAndIsActive(Long userId, boolean active);

    Optional<Rental> findByUserIdAndId(Long userId, Long rentalId);
}

