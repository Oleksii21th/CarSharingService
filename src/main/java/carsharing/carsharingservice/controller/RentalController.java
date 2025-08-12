package carsharing.carsharingservice.controller;

import carsharing.carsharingservice.model.Rental;
import carsharing.carsharingservice.service.RentalService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    public Rental createRental(@RequestBody Rental rental) {
        return rentalService.save(rental);
    }

    @GetMapping
    public List<Rental> getRentals(@RequestParam() Long user_id,
                                   @RequestParam() Boolean is_active) {
        return rentalService.findRentalsByUser(user_id, is_active);
    }

    @GetMapping("/{id}")
    public Rental getRentalById(@PathVariable Long id) {
        return rentalService.findRentalById(id);
    }

    @PostMapping("/{id}/return")
    public Rental returnRental(@PathVariable Long id,
                               @RequestBody Rental rental) {
        return rentalService.returnRental(id, rental);
    }
}
