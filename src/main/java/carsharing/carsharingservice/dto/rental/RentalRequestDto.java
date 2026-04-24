package carsharing.carsharingservice.dto.rental;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class RentalRequestDto {
    @NotNull
    private LocalDate rentalDate;
    @NotNull
    private LocalDate returnDate;
    @NotNull
    private Long carId;

    public RentalRequestDto(LocalDate rentalDate,
                            LocalDate returnDate,
                            Long carId) {
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.carId = carId;
    }

    public LocalDate getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(LocalDate rentalDate) {
        this.rentalDate = rentalDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }
}
