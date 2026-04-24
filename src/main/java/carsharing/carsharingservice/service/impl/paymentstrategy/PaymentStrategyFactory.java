package carsharing.carsharingservice.service.impl.paymentstrategy;

import carsharing.carsharingservice.exception.notfound.PaymentStrategyNotFoundException;
import carsharing.carsharingservice.model.PaymentType;
import carsharing.carsharingservice.service.PaymentStrategy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PaymentStrategyFactory {

    private final Map<PaymentType, PaymentStrategy> strategies = new HashMap<>();

    public PaymentStrategyFactory(List<PaymentStrategy> paymentStrategyList) {
        for (PaymentStrategy strategy : paymentStrategyList) {
            strategies.put(strategy.getPaymentType(), strategy);
        }
    }

    public PaymentStrategy get(PaymentType type) {
        PaymentStrategy strategy = strategies.get(type);

        if (strategy == null) {
            throw new PaymentStrategyNotFoundException(type);
        }

        return strategy;
    }
}
