package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.model.Car;
import carsharing.carsharingservice.model.Rental;
import carsharing.carsharingservice.repository.CarRepository;
import carsharing.carsharingservice.repository.RentalRepository;
import carsharing.carsharingservice.service.RentalService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;

    public RentalServiceImpl(RentalRepository rentalRepository,
                             CarRepository carRepository) {
        this.rentalRepository = rentalRepository;
        this.carRepository = carRepository;
    }

    @Override
    public Rental save(Rental rental) {
    /** TODO: adding functionality for decreasing inventory of car**/
        return rentalRepository.save(rental);
    }

    @Override
    public List<Rental> findRentalsByUser(Long userId, Boolean isActive) {
        return rentalRepository.findByUserIdAndActive(userId, isActive);
    }

    @Override
    public Rental findRentalById(Long id) {
        Optional<Rental> rentalOptional = rentalRepository.findById(id);
        if (rentalOptional.isEmpty()) {
            throw new RuntimeException("Rental not found with id: " + id);
        }
        return rentalOptional.get();
    }

    @Override
    public Rental returnRental(Long userId, Rental rental) {
        Long rentalId = rental.getId();
        Rental existingRental  = rentalRepository.findByUserIdAndRentalId(userId, rentalId)
                .orElseThrow(() ->
                        new RuntimeException("Rental no found with id " + rentalId));

        if (!rental.isActive()) {
            throw new RuntimeException("This rental has been returned and can't be returned again");
        }

        rental.setActualReturnDate(LocalDate.now());
        rental.setActive(false);
        Car car = existingRental.getCar();
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
        return rentalRepository.save(existingRental);
    }
}
