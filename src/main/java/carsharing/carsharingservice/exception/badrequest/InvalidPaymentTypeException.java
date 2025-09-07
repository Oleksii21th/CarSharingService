package carsharing.carsharingservice.exception.badrequest;

public class InvalidPaymentTypeException extends BadRequestException {
    public InvalidPaymentTypeException(String value) {
        super("Invalid payment type: " + value);
    }
}
