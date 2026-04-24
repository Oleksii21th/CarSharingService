package carsharing.carsharingservice.exception.notfound;

public class PaymentAlreadyCompletedException extends RuntimeException {
    public PaymentAlreadyCompletedException(Long rentalId) {
        super("Payment already completed for rental: " + rentalId);
    }
}
