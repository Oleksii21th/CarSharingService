package carsharing.carsharingservice.validation;

import carsharing.carsharingservice.exception.InvalidCarTypeException;
import carsharing.carsharingservice.model.CarType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class CarTypeValidator implements ConstraintValidator<ValidCarType, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        Arrays.stream(CarType.values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidCarTypeException(value));

        return true;
    }
}
