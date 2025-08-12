package carsharing.carsharingservice.controller;

import carsharing.carsharingservice.model.Payment;
import carsharing.carsharingservice.service.PaymentService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public List<Payment> findAll(@RequestParam(required = false) Long user_id) {
        return paymentService.findAllPayments(user_id);
    }

    @PostMapping
    public Payment createPayment(@RequestBody Payment payment) {
        return paymentService.savePaymentSession(payment);
    }

    @GetMapping("/success")
    public String paymentSuccess() {
        return paymentService.successPayment();
    }

    @GetMapping("/cancel")
    public String paymentCancel() {
        return paymentService.cancelPayment();
    }
}
