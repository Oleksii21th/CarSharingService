package carsharing.carsharingservice.security;

import carsharing.carsharingservice.dto.user.UserLoginRequestDto;
import carsharing.carsharingservice.dto.user.UserLoginResponseDto;
import carsharing.carsharingservice.exception.LoginException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public UserLoginResponseDto authenticateUser(UserLoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(), request.password()));
            String token = jwtUtil.generateToken(authentication);
            return new UserLoginResponseDto(token);
        } catch (BadCredentialsException ex) {
            throw new LoginException("Incorrect login or password");
        }
    }
}
