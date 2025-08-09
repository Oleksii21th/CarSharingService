package carsharing.carsharingservice.repository;

import carsharing.carsharingservice.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
