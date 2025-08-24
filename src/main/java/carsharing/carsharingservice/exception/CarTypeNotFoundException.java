package carsharing.carsharingservice.exception;

public class CarTypeNotFoundException extends RuntimeException {
    public CarTypeNotFoundException(String value) {
        super("Invalid car type: " + value);
    }
}
