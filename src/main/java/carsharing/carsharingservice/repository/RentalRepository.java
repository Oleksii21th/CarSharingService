package carsharing.carsharingservice.repository;

import carsharing.carsharingservice.model.Rental;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByIsActive(boolean active);

    List<Rental> findByUserIdAndIsActive(Long userId, boolean active);

    Optional<Rental> findByUserIdAndId(Long userId, Long rentalId);

    @Query("SELECT r FROM Rental r "
            + "WHERE r.returnDate <= :today "
            + "AND r.actualReturnDate IS NULL")
    List<Rental> findOverdueRentals(@Param("today") LocalDate today);
}
