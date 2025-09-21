package carsharing.carsharingservice.exception.notfound;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(Long userId) {
        super("There is no user with id " + userId);
    }

    public UserNotFoundException(String email) {
        super("There is no user with email " + email);
    }
}
