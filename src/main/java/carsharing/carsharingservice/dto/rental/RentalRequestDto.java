package carsharing.carsharingservice.dto.rental;

import jakarta.validation.constraints.NotNull;

public class RentalRequestDto {
    @NotNull
    private String rentalDate;
    @NotNull
    private String returnDate;
    @NotNull
    private Long carId;

    public RentalRequestDto(String rentalDate,
                            String returnDate,
                            Long carId) {
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.carId = carId;
    }

    public String getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(String rentalDate) {
        this.rentalDate = rentalDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }
}
