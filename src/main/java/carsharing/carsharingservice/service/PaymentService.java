package carsharing.carsharingservice.service;

import carsharing.carsharingservice.model.Payment;
import java.util.List;

public interface PaymentService {
    List<Payment> findAll();

    Payment save();

    boolean successPayment(String id);

    String cancelPayment();
}
