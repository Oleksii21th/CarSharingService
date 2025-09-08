package carsharing.carsharingservice.service;

import carsharing.carsharingservice.dto.car.CarRequestDto;
import carsharing.carsharingservice.dto.car.CarResponseDto;
import java.util.List;

public interface CarService {
    CarResponseDto save(CarRequestDto carDto);

    List<CarResponseDto> findAll();

    CarResponseDto findById(Long id);

    CarResponseDto updateCar(Long id, CarRequestDto updatedCarDto);

    void deleteCar(Long id);
}