package carsharing.carsharingservice.exception.badrequest;

public class ActivePaymentsException extends BadRequestException {
    public ActivePaymentsException(Long rentalId) {
        super(String.format("You have pending payments for rental with id: %s. " +
                "Please pay it first", rentalId));
    }
}
