package carsharing.carsharingservice.controller;

import carsharing.carsharingservice.dto.user.RoleUpdateRequestDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import carsharing.carsharingservice.dto.user.UserUpdateRequestDto;
import carsharing.carsharingservice.dto.user.UserWithRoleResponseDto;
import carsharing.carsharingservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Update user role", description = "Requires MANAGER role")
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/role")
    public UserWithRoleResponseDto updateUserRole(@PathVariable("id") Long userId,
                                                  @RequestBody RoleUpdateRequestDto updatedRole) {
        return userService.updateUserRole(userId, updatedRole);
    }

    @Operation(summary = "Get current user's profile")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @GetMapping("/me")
    public UserResponseDto getMyProfile(Authentication authentication) {
        return userService.getProfile(authentication);
    }

    @Operation(summary = "Update current user's profile")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @PatchMapping("/me")
    public UserResponseDto updateMyProfile(Authentication authentication,
                                           @RequestBody UserUpdateRequestDto userDto) {
        return userService.updateProfile(authentication, userDto);
    }
}
