package carsharing.carsharingservice.controller;

import carsharing.carsharingservice.dto.car.CarRequestDto;
import carsharing.carsharingservice.dto.car.CarResponseDto;
import carsharing.carsharingservice.service.CarService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public CarResponseDto saveCar(@RequestBody @Valid CarRequestDto requestDto) {
        return carService.save(requestDto);
    }

    @GetMapping
    public List<CarResponseDto> findAll() {
        return carService.findAll();
    }

    @GetMapping("/{id}")
    public CarResponseDto findById(@PathVariable Long id) {
        return carService.findById(id);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{id}")
    public CarResponseDto updateCar(@PathVariable Long id,
                                    @RequestBody @Valid CarRequestDto requestDto) {
        return carService.updateCar(id, requestDto);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}
