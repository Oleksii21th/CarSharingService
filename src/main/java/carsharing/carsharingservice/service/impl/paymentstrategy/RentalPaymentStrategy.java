package carsharing.carsharingservice.service.impl.paymentstrategy;

import carsharing.carsharingservice.model.PaymentType;
import carsharing.carsharingservice.model.Rental;
import carsharing.carsharingservice.service.PaymentStrategy;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class RentalPaymentStrategy implements PaymentStrategy {

    private final BigDecimal dailyFeeMultiplier = BigDecimal.ONE;

    @Override
    public BigDecimal calculateAmount(Rental rental) {
        long days =
                ChronoUnit.DAYS.between(rental.getRentalDate(),
                rental.getReturnDate());

        return rental.getCar().getDailyFee()
                .multiply(BigDecimal.valueOf(days))
                .multiply(dailyFeeMultiplier);
    }

    @Override
    public PaymentType getPaymentType() {
        return PaymentType.PAYMENT;
    }
}
