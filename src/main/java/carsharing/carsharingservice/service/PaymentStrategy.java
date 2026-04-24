package carsharing.carsharingservice.service;

import carsharing.carsharingservice.model.PaymentType;
import carsharing.carsharingservice.model.Rental;
import java.math.BigDecimal;

public interface PaymentStrategy {
    BigDecimal calculateAmount(Rental rental);

    PaymentType getPaymentType();
}
