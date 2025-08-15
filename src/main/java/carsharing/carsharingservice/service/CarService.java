package carsharing.carsharingservice.service;

import carsharing.carsharingservice.model.Car;
import java.util.List;

public interface CarService {
    Car save(Car car);

    List<Car> findAll();

    Car findById(Long id);

    Car updateCar(Long id, Car update);

    void deleteCar(Long id);
}