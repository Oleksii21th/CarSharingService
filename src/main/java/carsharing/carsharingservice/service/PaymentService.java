package carsharing.carsharingservice.service;

import carsharing.carsharingservice.model.Payment;
import java.util.List;

public interface PaymentService {
    List<Payment> findAllPayments(Long userId);

    Payment savePaymentSession(Payment payment);

    String successPayment();

    String cancelPayment();
}
