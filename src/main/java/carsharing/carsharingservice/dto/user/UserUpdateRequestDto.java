package carsharing.carsharingservice.dto.user;

import jakarta.validation.constraints.Email;

public record UserUpdateRequestDto(
        @Email String email,
        String firstName,
        String lastName
) {}
