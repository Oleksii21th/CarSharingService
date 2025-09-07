package carsharing.carsharingservice.service;

import carsharing.carsharingservice.dto.rental.RentalRequestDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.dto.rental.RentalSearchParametersDto;
import carsharing.carsharingservice.dto.rental.RentalReturnDateDto;
import java.util.List;

public interface RentalService {
    RentalResponseDto save(Long userId, RentalRequestDto rentalDto);

    List<RentalResponseDto> findRentalsByUser(RentalSearchParametersDto paramsDto);

    RentalResponseDto findRentalById(Long id);

    RentalResponseDto returnRental(Long id, RentalReturnDateDto returnDateDto);
}
