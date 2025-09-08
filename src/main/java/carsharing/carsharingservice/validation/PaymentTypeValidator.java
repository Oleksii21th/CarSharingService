package carsharing.carsharingservice.validation;

import carsharing.carsharingservice.exception.badrequest.InvalidPaymentTypeException;
import carsharing.carsharingservice.model.PaymentType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PaymentTypeValidator implements ConstraintValidator<ValidPaymentType, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            PaymentType.valueOf(value);
            return true;
        } catch (IllegalArgumentException e) {
            throw new InvalidPaymentTypeException(value);
        }
    }
}