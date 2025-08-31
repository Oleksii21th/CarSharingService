package carsharing.carsharingservice.exception.badrequest;

public class InvalidRentalDateException extends BadRequestException {
    public InvalidRentalDateException() {
        super("Rental date cannot be before the current date");
    }
}
