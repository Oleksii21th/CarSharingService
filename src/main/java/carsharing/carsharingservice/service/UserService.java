package carsharing.carsharingservice.service;

import carsharing.carsharingservice.dto.user.RoleUpdateRequestDto;
import carsharing.carsharingservice.dto.user.UserRegistrationRequestDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import carsharing.carsharingservice.dto.user.UserWithRoleResponseDto;
import org.springframework.security.core.Authentication;

public interface UserService {
    UserWithRoleResponseDto updateUserRole(Long userId, RoleUpdateRequestDto updatedRole);

    UserResponseDto getProfile(Authentication authentication);

    UserResponseDto updateProfile(Authentication authentication, UserRegistrationRequestDto userDto);

    UserResponseDto registerUser(UserRegistrationRequestDto userDto);
}
