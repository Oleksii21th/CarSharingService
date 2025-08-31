package carsharing.carsharingservice.exception.badrequest;

public class EmptyCarInventoryException extends BadRequestException {
    public EmptyCarInventoryException(String carModel) {
        super(String.format("Car %s is unavailable, " +
                "contact the administrator", carModel));
    }
}
