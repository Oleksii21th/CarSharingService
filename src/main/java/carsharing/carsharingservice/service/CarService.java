package carsharing.carsharingservice.service;

import carsharing.carsharingservice.model.Car;
import java.util.List;
import java.util.Optional;

public interface CarService {
    Car save(Car car);

    List<Car> findAll();

    Car findById(Long id);

    Car updateCar(Long id, Car update);

    void deleteCar(Long id);
}