package carsharing.carsharingservice.service.impl.paymentstrategy;

import carsharing.carsharingservice.model.PaymentType;
import carsharing.carsharingservice.model.Rental;
import carsharing.carsharingservice.service.PaymentStrategy;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class FinePaymentStrategy implements PaymentStrategy {

    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(2);

    @Override
    public BigDecimal calculateAmount(Rental rental) {

        long overdueDays =
                ChronoUnit.DAYS.between(rental.getReturnDate(),
                rental.getActualReturnDate());

        return rental.getCar().getDailyFee()
                .multiply(BigDecimal.valueOf(overdueDays))
                .multiply(FINE_MULTIPLIER);
    }

    @Override
    public PaymentType getPaymentType() {
        return PaymentType.FINE;
    }
}
