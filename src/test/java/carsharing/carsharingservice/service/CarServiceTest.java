package carsharing.carsharingservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import carsharing.carsharingservice.dto.car.CarRequestDto;
import carsharing.carsharingservice.dto.car.CarResponseDto;
import carsharing.carsharingservice.exception.notfound.CarNotFoundException;
import carsharing.carsharingservice.mapper.CarMapper;
import carsharing.carsharingservice.model.Car;
import carsharing.carsharingservice.model.CarType;
import carsharing.carsharingservice.repository.CarRepository;
import carsharing.carsharingservice.service.impl.CarServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    private Car car;
    private CarRequestDto carRequestDto;
    private CarResponseDto carResponseDto;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setId(1L);
        car.setBrand("Test");
        car.setModel("Test 2");
        car.setType(CarType.SEDAN);
        car.setInventory(5);
        car.setDailyFee(BigDecimal.valueOf(199.99));

        carRequestDto = new CarRequestDto();
        carRequestDto.setBrand("Test");
        carRequestDto.setModel("Test 2");
        carRequestDto.setType("SEDAN");
        carRequestDto.setInventory(5);
        carRequestDto.setDailyFee(BigDecimal.valueOf(199.99));

        carResponseDto = new CarResponseDto();
        carResponseDto.setId(1L);
        carResponseDto.setBrand("Test");
        carResponseDto.setModel("Test 2");
        carResponseDto.setType("SEDAN");
        carResponseDto.setDailyFee(BigDecimal.valueOf(199.99));
    }

    @Test
    @DisplayName("Saves car and returns DTO")
    void save_ValidDto_ReturnsSavedDto() {
        when(carMapper.toModel(carRequestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carResponseDto);

        CarResponseDto result = carService.save(carRequestDto);

        assertThat(result).isEqualTo(carResponseDto);
        verify(carRepository).save(car);
    }

    @Test
    @DisplayName("Returns all cars as DTOs")
    void findAll_ReturnsListOfCarDto() {
        when(carRepository.findAll()).thenReturn(List.of(car));
        when(carMapper.toDto(car)).thenReturn(carResponseDto);

        List<CarResponseDto> result = carService.findAll();

        assertThat(result).hasSize(1).containsExactly(carResponseDto);
        verify(carRepository).findAll();
        verify(carMapper).toDto(car);
    }

    @Test
    @DisplayName("Returns car by id")
    void findById_ExistingId_ReturnsCarDto() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(carResponseDto);

        CarResponseDto result = carService.findById(1L);

        assertThat(result).isEqualTo(carResponseDto);
        verify(carRepository).findById(1L);
    }

    @Test
    @DisplayName("Throws CarNotFoundException if id not found")
    void findById_NonExistingId_ThrowsException() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class,
                () -> carService.findById(99L));
        verify(carRepository).findById(99L);
    }

    @Test
    @DisplayName("Updates existing car and returns DTO")
    void updateCar_ExistingId_UpdatesAndReturnsDto() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carResponseDto);

        CarResponseDto result = carService.updateCar(1L, carRequestDto);

        assertThat(result).isEqualTo(carResponseDto);
        verify(carMapper).updateCarFromDto(carRequestDto, car);
        verify(carRepository).save(car);
    }

    @Test
    @DisplayName("Throws CarNotFoundException when updating non-existing car")
    void updateCar_NonExistingId_ThrowsException() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class,
                () -> carService.updateCar(99L, carRequestDto));
        verify(carRepository).findById(99L);
    }

    @Test
    @DisplayName("Deletes car by id")
    void deleteCar_ValidId_DeletesCar() {
        when(carRepository.existsById(1L)).thenReturn(true);

        carService.deleteCar(1L);

        verify(carRepository).existsById(1L);
        verify(carRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Throws CarNotFoundException when deleting non-existing car")
    void deleteCar_NonExistingId_ThrowsException() {
        when(carRepository.existsById(99L)).thenReturn(false);

        assertThrows(CarNotFoundException.class,
                () -> carService.deleteCar(99L));
        verify(carRepository).existsById(99L);
    }
}
