package carsharing.carsharingservice.service;

import carsharing.carsharingservice.dto.user.UserRegistrationRequestDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import carsharing.carsharingservice.model.Role;
import carsharing.carsharingservice.model.User;
import org.springframework.security.core.Authentication;

public interface UserService {
    User updateUserRole(Long id, Role role);

    UserResponseDto getProfile(Authentication authentication);

    UserResponseDto updateProfile(Authentication authentication, UserRegistrationRequestDto userDto);

    UserResponseDto registerUser(UserRegistrationRequestDto userDto);
}
