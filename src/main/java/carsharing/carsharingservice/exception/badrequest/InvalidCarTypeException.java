package carsharing.carsharingservice.exception.badrequest;

public class InvalidCarTypeException extends BadRequestException {
    public InvalidCarTypeException(String value) {
        super("Invalid car type: " + value);
    }
}
