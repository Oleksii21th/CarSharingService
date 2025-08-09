package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.model.Payment;
import carsharing.carsharingservice.service.PaymentService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Override
    public List<Payment> findAll() {
        return List.of();
    }

    @Override
    public Payment save() {
        return null;
    }

    @Override
    public boolean successPayment(String id) {
        return false;
    }

    @Override
    public String cancelPayment() {
        return "";
    }
}
