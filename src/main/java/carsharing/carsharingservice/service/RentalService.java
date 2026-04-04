package carsharing.carsharingservice.service;

import carsharing.carsharingservice.dto.rental.RentalRequestDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.dto.rental.RentalSearchParametersDto;
import java.util.List;
import org.springframework.security.core.Authentication;

public interface RentalService {
    RentalResponseDto save(Long userId, RentalRequestDto rentalDto);

    List<RentalResponseDto> findRentalsByUser(RentalSearchParametersDto paramsDto, Authentication authentication);

    RentalResponseDto findRentalById(Long id, Authentication authentication);

    RentalResponseDto returnRental(Long userId, Long rentalId);
}
