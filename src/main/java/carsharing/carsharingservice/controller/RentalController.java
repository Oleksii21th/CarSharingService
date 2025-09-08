package carsharing.carsharingservice.controller;

import carsharing.carsharingservice.dto.rental.RentalRequestDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.dto.rental.RentalSearchParametersDto;
import carsharing.carsharingservice.dto.rental.RentalReturnDateDto;
import carsharing.carsharingservice.service.RentalService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    public RentalResponseDto createRental(Long userId,
                                          @RequestBody RentalRequestDto rentalDto) {
        return rentalService.save(userId, rentalDto);
    }

    @GetMapping
    public List<RentalResponseDto> findRentalsByUser(RentalSearchParametersDto paramsDto) {
        return rentalService.findRentalsByUser(paramsDto);
    }

    @GetMapping("/{id}")
    public RentalResponseDto findRentalById(@PathVariable Long id) {
        return rentalService.findRentalById(id);
    }

    @PostMapping("/{id}/return")
    public RentalResponseDto returnRental(@PathVariable Long id,
                                          @RequestBody RentalReturnDateDto returnDateDto) {
        return rentalService.returnRental(id, returnDateDto);
    }
}
