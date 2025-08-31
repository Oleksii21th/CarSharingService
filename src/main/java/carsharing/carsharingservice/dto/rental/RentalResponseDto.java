package carsharing.carsharingservice.dto.rental;

import carsharing.carsharingservice.dto.car.CarResponseDto;

public class RentalResponseDto {
    private Long id;
    private String rentalDate;
    private String returnDate;
    private String actualReturnDate;
    private CarResponseDto car;

    public RentalResponseDto(Long id,
                             String rentalDate,
                             String returnDate,
                             String actualReturnDate,
                             CarResponseDto car) {
        this.id = id;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.actualReturnDate = actualReturnDate;
        this.car = car;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(String actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public CarResponseDto getCar() {
        return car;
    }

    public void setCar(CarResponseDto car) {
        this.car = car;
    }
}
