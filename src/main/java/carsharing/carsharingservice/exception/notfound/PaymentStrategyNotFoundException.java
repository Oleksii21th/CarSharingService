package carsharing.carsharingservice.exception.notfound;

import carsharing.carsharingservice.model.PaymentType;

public class PaymentStrategyNotFoundException extends RuntimeException {
    public PaymentStrategyNotFoundException(PaymentType type) {
        super("No payment strategy found for type: " + type);
    }
}
