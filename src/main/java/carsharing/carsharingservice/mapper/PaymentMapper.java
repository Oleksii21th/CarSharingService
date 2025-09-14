package carsharing.carsharingservice.mapper;

import carsharing.carsharingservice.config.MapperConfig;
import carsharing.carsharingservice.dto.payment.PaymentResponseDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseFullInfoDto;
import carsharing.carsharingservice.model.Payment;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = RentalMapper.class)
public interface PaymentMapper {
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "rentalId", source = "rental.id")
    PaymentResponseDto toDto(Payment payment);

    PaymentResponseFullInfoDto toFullInfoDto(Payment payment);

    @AfterMapping
    default void setUserIdAndType(@MappingTarget PaymentResponseFullInfoDto responseFullInfoDto,
                                  Payment payment) {
        responseFullInfoDto.setType(payment.getType().name());
        responseFullInfoDto.setUserId(payment.getRental().getUser().getId());
    }

    @AfterMapping
    default void setStatusAndType(@MappingTarget PaymentResponseDto responseDto, Payment payment) {
        responseDto.setType(payment.getType().name());
        responseDto.setStatus(payment.getStatus().name());
    }
}
