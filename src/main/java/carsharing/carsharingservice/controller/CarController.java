package carsharing.carsharingservice.controller;

import carsharing.carsharingservice.dto.car.CarRequestDto;
import carsharing.carsharingservice.dto.car.CarResponseDto;
import carsharing.carsharingservice.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @Operation(summary = "Add a new car", description = "Requires MANAGER role")
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public CarResponseDto saveCar(@RequestBody @Valid CarRequestDto requestDto) {
        return carService.save(requestDto);
    }

    @Operation(summary = "Get all cars")
    @GetMapping
    public List<CarResponseDto> findAll() {
        return carService.findAll();
    }

    @Operation(summary = "Get a car by ID")
    @GetMapping("/{id}")
    public CarResponseDto findById(@PathVariable Long id) {
        return carService.findById(id);
    }

    @Operation(summary = "Update a car", description = "Requires MANAGER role")
    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{id}")
    public CarResponseDto updateCar(@PathVariable Long id,
                                    @RequestBody @Valid CarRequestDto requestDto) {
        return carService.updateCar(id, requestDto);
    }

    @Operation(summary = "Delete a car", description = "Requires MANAGER role")
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}
