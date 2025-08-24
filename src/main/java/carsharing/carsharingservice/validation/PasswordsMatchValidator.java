package carsharing.carsharingservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanUtils;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, Object> {
    private String passwordField;
    private String repeatPasswordField;

    @Override
    public void initialize(PasswordsMatch constraintAnnotation) {
        this.passwordField = constraintAnnotation.passwordField();
        this.repeatPasswordField = constraintAnnotation.repeatPasswordField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Object password =
                    BeanUtils.getPropertyDescriptor(value.getClass(), passwordField)
                    .getReadMethod().invoke(value);
            Object repeatPassword =
                    BeanUtils.getPropertyDescriptor(value.getClass(), repeatPasswordField)
                    .getReadMethod().invoke(value);

            return Objects.equals(password, repeatPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
