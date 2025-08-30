package carsharing.carsharingservice.exception;

public class UserAlreadyExistsRegistrationException extends RuntimeException {
    public UserAlreadyExistsRegistrationException(String email) {
        super(String.format("User with email %s already exists", email));
    }
}
