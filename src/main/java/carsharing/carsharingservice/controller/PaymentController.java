package carsharing.carsharingservice.controller;

import carsharing.carsharingservice.dto.payment.PaymentRequestDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseFullInfoDto;
import carsharing.carsharingservice.service.PaymentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @GetMapping
    public List<PaymentResponseDto> findAllPayments(@RequestParam(value = "user_id",
                                                                required = false) Long userId,
                                                    Authentication authentication) {
        return paymentService.findAllPayments(userId, authentication);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @PostMapping
    public PaymentResponseDto createPayment(@Valid @RequestBody PaymentRequestDto requestDto,
                                            Authentication authentication) {
        return paymentService.savePaymentSession(requestDto, authentication);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @GetMapping("/success")
    public PaymentResponseFullInfoDto paymentSuccess(@RequestParam("session_id") String sessionId) {
        return paymentService.updatePaymentStatus(sessionId);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @GetMapping("/cancel")
    public String paymentCancel() {
        return "You can complete this payment later, using the same session.";
    }
}
