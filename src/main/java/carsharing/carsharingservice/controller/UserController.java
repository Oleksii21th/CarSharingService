package carsharing.carsharingservice.controller;

import carsharing.carsharingservice.dto.user.RoleUpdateRequestDto;
import carsharing.carsharingservice.dto.user.UserRegistrationRequestDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import carsharing.carsharingservice.dto.user.UserWithRoleResponseDto;
import carsharing.carsharingservice.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/role")
    public UserWithRoleResponseDto updateUserRole(@PathVariable Long userId,
                                                  @RequestBody RoleUpdateRequestDto updatedRole) {
        return userService.updateUserRole(userId, updatedRole);
    }

    @PreAuthorize("hasAnyRole('USER', 'MANAGER')")
    @GetMapping("/me")
    public UserResponseDto getMyProfile(Authentication authentication) {
        return userService.getProfile(authentication);
    }

    @PreAuthorize("hasAnyRole('USER', 'MANAGER')")
    @PatchMapping("/me")
    public UserResponseDto updateMyProfile(Authentication authentication,
                                @RequestBody UserRegistrationRequestDto userDto) {
        return userService.updateProfile(authentication, userDto);
    }
}
