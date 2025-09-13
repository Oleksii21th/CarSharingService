package carsharing.carsharingservice.mapper;

import carsharing.carsharingservice.config.MapperConfig;
import carsharing.carsharingservice.dto.user.UserRegistrationRequestDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import carsharing.carsharingservice.dto.user.UserWithRoleResponseDto;
import carsharing.carsharingservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(source = "username", target = "email")
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);

    UserWithRoleResponseDto toDtoOnlyWithUpdatingRole(User user);
}
