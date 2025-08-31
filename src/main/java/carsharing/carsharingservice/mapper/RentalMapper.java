package carsharing.carsharingservice.mapper;

import carsharing.carsharingservice.config.MapperConfig;
import carsharing.carsharingservice.dto.rental.AddRentalRequestDto;
import carsharing.carsharingservice.dto.rental.RentalDetailsDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.model.Rental;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {CarMapper.class, UserMapper.class, DateMapper.class})
public interface RentalMapper {
    @Mapping(target = "rentalDate", source = "rentalDate", qualifiedByName = "formatDate")
    @Mapping(target = "returnDate", source = "returnDate", qualifiedByName = "formatDate")
    @Mapping(target = "car", source = "carId", qualifiedByName = "FindCarById")
    Rental toModel(AddRentalRequestDto rentalDto);

    @Mapping(target = "rentalDate", source = "rentalDate", qualifiedByName = "formatDate")
    @Mapping(target = "returnDate", source = "returnDate", qualifiedByName = "formatDate")
    @Mapping(target = "actualReturnDate", source = "actualReturnDate", qualifiedByName = "formatDate")
    RentalResponseDto toDto(Rental rental);

    @InheritConfiguration(name = "toDto")
    RentalDetailsDto toDetailsDto(Rental rental);
}
