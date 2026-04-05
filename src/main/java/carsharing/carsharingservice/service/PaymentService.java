package carsharing.carsharingservice.service;

import carsharing.carsharingservice.dto.payment.PaymentRequestDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseFullInfoDto;
import java.util.List;
import org.springframework.security.core.Authentication;

public interface PaymentService {
    List<PaymentResponseDto> findAllPayments(Long userId, Authentication authentication);

    PaymentResponseDto savePaymentSession(PaymentRequestDto requestDto, Authentication authentication);

    PaymentResponseFullInfoDto updatePaymentStatus(String sessionId);
}
