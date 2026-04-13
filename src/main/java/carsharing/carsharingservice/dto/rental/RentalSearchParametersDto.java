package carsharing.carsharingservice.dto.rental;

import jakarta.validation.constraints.NotNull;

public record RentalSearchParametersDto(Long userId,
                                        @NotNull boolean isActive) {
}
