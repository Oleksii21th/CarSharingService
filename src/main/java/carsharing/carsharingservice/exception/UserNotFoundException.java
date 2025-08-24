package carsharing.carsharingservice.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("There is no user with id " + userId);
    }
}
