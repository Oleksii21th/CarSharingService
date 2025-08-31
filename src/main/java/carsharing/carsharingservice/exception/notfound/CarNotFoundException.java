package carsharing.carsharingservice.exception.notfound;

public class CarNotFoundException extends EntityNotFoundException {
    public CarNotFoundException(Long id) {
        super("Car not found with id: " + id);
    }
}
