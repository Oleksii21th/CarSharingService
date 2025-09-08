package carsharing.carsharingservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PaymentTypeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPaymentType {
    String message() default "Invalid payment type. Must be PAYMENT or FINE";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}