package carsharing.carsharingservice.service;

import carsharing.carsharingservice.dto.payment.PaymentRequestDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseFullInfoDto;
import carsharing.carsharingservice.model.PaymentStatus;
import java.util.List;

public interface PaymentService {
    List<PaymentResponseDto> findAllPayments(Long userId);

    PaymentResponseDto savePaymentSession(PaymentRequestDto requestDto);

    PaymentResponseFullInfoDto updatePaymentStatus(String sessionId, PaymentStatus status);
}
