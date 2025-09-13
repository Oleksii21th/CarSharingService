package carsharing.carsharingservice.controller;

import carsharing.carsharingservice.dto.payment.PaymentRequestDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseFullInfoDto;
import carsharing.carsharingservice.model.PaymentStatus;
import carsharing.carsharingservice.service.PaymentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('USER', 'MANAGER')")
    @GetMapping
    public List<PaymentResponseDto> findAllPayments(@RequestParam("user_id") Long userId) {
        return paymentService.findAllPayments(userId);
    }

    @PreAuthorize("hasAnyRole('USER', 'MANAGER')")
    @PostMapping
    public PaymentResponseDto createPayment(@Valid @RequestBody PaymentRequestDto requestDto) {
        return paymentService.savePaymentSession(requestDto);
    }

    @PreAuthorize("hasAnyRole('USER', 'MANAGER')")
    @GetMapping("/success")
    public PaymentResponseFullInfoDto paymentSuccess(@RequestParam("session_id") String sessionId) {
        return paymentService.updatePaymentStatus(sessionId, PaymentStatus.PAID);
    }

    @PreAuthorize("hasAnyRole('USER', 'MANAGER')")
    @GetMapping("/cancel")
    public PaymentResponseFullInfoDto paymentCancel(@RequestParam("session_id") String sessionId) {
        return paymentService.updatePaymentStatus(sessionId, PaymentStatus.PENDING);
    }
}
