package carsharing.carsharingservice.repository;

import carsharing.carsharingservice.model.Payment;
import carsharing.carsharingservice.model.PaymentStatus;
import carsharing.carsharingservice.model.PaymentType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p WHERE p.rental.user.id = :userId AND p.isDeleted = false")
    List<Payment> findPaymentsByUserId(Long userId);
    @Query("SELECT p FROM Payment p WHERE p.rental.id = :rentalId AND p.type = :type AND p.isDeleted = false")
    List<Payment> findByRentalIdAndType(Long rentalId, PaymentType type);
    Optional<Payment> findBySessionId(String sessionId);
    @Query("SELECT p FROM Payment p JOIN p.rental r JOIN r.user u WHERE u.id = :userId AND p.status = :status")
    List<Payment> findByUserIdAndStatus(Long userId, PaymentStatus status);
}

