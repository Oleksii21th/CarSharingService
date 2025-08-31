package carsharing.carsharingservice.exception.notfound;

public class RentalNotFoundException extends EntityNotFoundException {
    public RentalNotFoundException(Long id) {
      super("Rental not found with id: " + id);
    }
}
