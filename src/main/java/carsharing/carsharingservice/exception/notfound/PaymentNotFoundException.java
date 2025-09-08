package carsharing.carsharingservice.exception.notfound;

public class PaymentNotFoundException extends EntityNotFoundException {
    public PaymentNotFoundException(Long id) {
        super("Payment not found with id: " + id);
    }

    public PaymentNotFoundException(String sessionId) {
        super("Payment not found for session: " + sessionId);
    }
}
