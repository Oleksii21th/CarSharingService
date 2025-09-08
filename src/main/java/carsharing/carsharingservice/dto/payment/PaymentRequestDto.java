package carsharing.carsharingservice.dto.payment;

import carsharing.carsharingservice.validation.ValidPaymentType;
import jakarta.validation.constraints.NotNull;

public record PaymentRequestDto(@NotNull Long rentalId, @NotNull @ValidPaymentType String paymentType) {
}
