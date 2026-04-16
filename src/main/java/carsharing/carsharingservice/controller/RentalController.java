package carsharing.carsharingservice.controller;

import carsharing.carsharingservice.dto.rental.RentalDetailsDto;
import carsharing.carsharingservice.dto.rental.RentalRequestDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.dto.rental.RentalSearchParametersDto;
import carsharing.carsharingservice.model.User;
import carsharing.carsharingservice.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @Operation(summary = "Create a new rental")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @PostMapping
    public RentalResponseDto createRental(Authentication authentication,
                                          @RequestBody @Valid RentalRequestDto rentalDto) {
        User user = (User) authentication.getPrincipal();
        return rentalService.save(user.getId(), rentalDto);
    }

    @Operation(summary = "Get rentals for authenticated user")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @GetMapping
    public List<RentalResponseDto> findRentalsByUser(
            @Valid @ModelAttribute RentalSearchParametersDto paramsDto,
            Authentication authentication) {
        return rentalService.findRentalsByUser(paramsDto, authentication);
    }

    @Operation(summary = "Get rental by ID")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @GetMapping("/{id}")
    public RentalDetailsDto findRentalById(@PathVariable Long id,
                                           Authentication authentication) {
        return rentalService.findRentalById(id, authentication);
    }

    @Operation(summary = "Return a rental")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @PostMapping("/{id}/return")
    public RentalResponseDto returnRental(@PathVariable("id") Long rentalId,
                                          Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return rentalService.returnRental(user.getId(), rentalId);
    }
}
