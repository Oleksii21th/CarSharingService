package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.dto.car.AddCarRequestDto;
import carsharing.carsharingservice.dto.car.CarResponseDto;
import carsharing.carsharingservice.exception.CarNotFoundException;
import carsharing.carsharingservice.mapper.CarMapper;
import carsharing.carsharingservice.model.Car;
import carsharing.carsharingservice.repository.CarRepository;
import carsharing.carsharingservice.service.CarService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    public CarServiceImpl(CarRepository carRepository, CarMapper carMapper) {
        this.carRepository = carRepository;
        this.carMapper = carMapper;
    }

    @Override
    public CarResponseDto save(AddCarRequestDto dto) {
        Car car = carMapper.toModel(dto);
        Car savedCar = carRepository.save(car);
        return carMapper.toDto(savedCar);
    }

    @Override
    public List<CarResponseDto> findAll() {
        return carRepository.findAll().stream()
                .map(carMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CarResponseDto findById(Long id) {
        Optional<Car> carOptional = carRepository.findById(id);
        if (carOptional.isEmpty()) {
            throw new CarNotFoundException(id);
        }
        return carOptional.map(carMapper::toDto).orElse(null);
    }

    @Override
    public CarResponseDto updateCar(Long id, AddCarRequestDto dto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));

        carMapper.updateCarFromDto(dto, car);
        Car updatedCar = carRepository.save(car);
        return carMapper.toDto(updatedCar);
    }

    @Override
    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new CarNotFoundException(id);
        }
        carRepository.deleteById(id);
    }
}
