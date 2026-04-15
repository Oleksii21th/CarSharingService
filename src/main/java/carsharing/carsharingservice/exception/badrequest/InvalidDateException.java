package carsharing.carsharingservice.exception.badrequest;

public class InvalidDateException extends BadRequestException {
    public InvalidDateException(String message) {
        super(message);
    }
}
