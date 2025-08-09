package carsharing.carsharingservice.repository;

import carsharing.carsharingservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}

