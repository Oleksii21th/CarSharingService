package carsharing.carsharingservice.exception;

public class LoginFailedException extends RuntimeException {
    public LoginFailedException() {
        super("Incorrect login or password");
    }
}
