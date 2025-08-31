package carsharing.carsharingservice.exception.badrequest;

public class TwiceReturnedRentalException extends BadRequestException {
    public TwiceReturnedRentalException() {
        super("This rental has already been returned");
    }
}
