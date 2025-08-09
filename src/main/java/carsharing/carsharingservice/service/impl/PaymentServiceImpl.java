package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.model.Payment;
import carsharing.carsharingservice.model.PaymentStatus;
import carsharing.carsharingservice.repository.PaymentRepository;
import carsharing.carsharingservice.service.PaymentService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> findAllPayments(Long userId) {
        return paymentRepository.findAll();
    }

    public Payment savePaymentSession(Payment payment) {
        payment.setStatus(PaymentStatus.PENDING);
        return paymentRepository.save(payment);
    }

    @Override
    public String successPayment() {
        return "Payment success";
    }

    @Override
    public String cancelPayment() {
        return "Payment canceled";
    }
}
