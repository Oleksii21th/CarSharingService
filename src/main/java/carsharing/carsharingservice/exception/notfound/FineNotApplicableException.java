package carsharing.carsharingservice.exception.notfound;

public class FineNotApplicableException extends RuntimeException {
    public FineNotApplicableException(Long rentalId) {
        super("Fine cannot be applied. Rental is not overdue: " + rentalId);
    }
}
