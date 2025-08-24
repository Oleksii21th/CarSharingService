package carsharing.carsharingservice.mapper;

import carsharing.carsharingservice.config.MapperConfig;
import carsharing.carsharingservice.dto.car.AddCarRequestDto;
import carsharing.carsharingservice.dto.car.CarResponseDto;
import carsharing.carsharingservice.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    Car toModel(AddCarRequestDto carDto);

    CarResponseDto toDto(Car car);

    void updateCarFromDto(AddCarRequestDto dto, @MappingTarget Car car);
}
