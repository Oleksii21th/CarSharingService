package carsharing.carsharingservice.exception;

public class InvalidCarTypeException extends RuntimeException {
    public InvalidCarTypeException(String value) {
        super("Invalid car type: " + value);
    }
}
