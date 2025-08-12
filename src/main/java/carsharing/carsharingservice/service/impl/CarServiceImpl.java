package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.model.Car;
import carsharing.carsharingservice.repository.CarRepository;
import carsharing.carsharingservice.service.CarService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CarServiceImpl implements CarService {
    private final CarRepository repository;

    public CarServiceImpl(CarRepository repository) {
        this.repository = repository;
    }

    @Override
    public Car save(Car car) {
        return repository.save(car);
    }

    @Override
    public List<Car> findAll() {
        return repository.findAll();
    }

    @Override
    public Car findById(Long id) {
        Optional<Car> carOptional = repository.findById(id);
        if (carOptional.isEmpty()) {
            throw new RuntimeException("Car not found with id: " + id);
        }

        return carOptional.get();
    }

    @Override
    public Car updateCar(Long id, Car update) {
        Car car = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + id));

        if (update.getModel() != null) {
            car.setModel(update.getModel());
        }
        if (update.getBrand() != null) {
            car.setBrand(update.getBrand());
        }
        if (update.getType() != null) {
            car.setType(update.getType());
        }
        if (update.getInventory() >= 0) {
            car.setInventory(update.getInventory());
        }
        if (update.getDailyFee() != null) {
            car.setDailyFee(update.getDailyFee());
        }

        return repository.save(car);
    }

    @Override
    public void deleteCar(Long id) {
        checkCarExistsOrThrowException(id);
        repository.deleteById(id);
    }

    private void checkCarExistsOrThrowException(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Car not found with id: " + id);
        }
    }
}
