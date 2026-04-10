package carsharing.carsharingservice.controller;

import carsharing.carsharingservice.dto.user.UserLoginRequestDto;
import carsharing.carsharingservice.dto.user.UserLoginResponseDto;
import carsharing.carsharingservice.dto.user.UserRegistrationRequestDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import carsharing.carsharingservice.security.AuthenticationService;
import carsharing.carsharingservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(UserService userService,
                                    AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/registration")
    public UserResponseDto register(@Valid @RequestBody UserRegistrationRequestDto request) {
        return userService.registerUser(request);
    }

    @Operation(summary = "Login a user")
    @PostMapping("/login")
    public UserLoginResponseDto login(@Valid @RequestBody UserLoginRequestDto request) {
        return authenticationService.authenticateUser(request);
    }
}
