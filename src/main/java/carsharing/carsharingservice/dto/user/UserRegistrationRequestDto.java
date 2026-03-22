package carsharing.carsharingservice.dto.user;

import carsharing.carsharingservice.validation.PasswordsMatch;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordsMatch(passwordField = "password",
        repeatPasswordField = "repeatPassword",
        message = "Passwords don't match")
public record UserRegistrationRequestDto(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8)
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,
        @NotBlank
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String repeatPassword,
        @NotBlank String firstName,
        @NotBlank String lastName) {
}
