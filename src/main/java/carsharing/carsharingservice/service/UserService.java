package carsharing.carsharingservice.service;

import carsharing.carsharingservice.dto.user.UserRegistrationRequestDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import carsharing.carsharingservice.model.Role;
import carsharing.carsharingservice.model.User;

public interface UserService {
    User updateUserRole(Long id, Role role);

    User getProfile();

    User updateProfile(User user);

    UserResponseDto registerUser(UserRegistrationRequestDto request);
}
