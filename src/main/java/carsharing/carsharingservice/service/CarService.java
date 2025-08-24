package carsharing.carsharingservice.service;

import carsharing.carsharingservice.dto.car.AddCarRequestDto;
import carsharing.carsharingservice.dto.car.CarResponseDto;
import java.util.List;

public interface CarService {
    CarResponseDto save(AddCarRequestDto carDto);

    List<CarResponseDto> findAll();

    CarResponseDto findById(Long id);

    CarResponseDto updateCar(Long id, AddCarRequestDto updatedCarDto);

    void deleteCar(Long id);
}