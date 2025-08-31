package carsharing.carsharingservice.dto.rental;

import jakarta.validation.constraints.NotNull;

public record RentalSearchParametersDto(@NotNull Long userId,
                                        @NotNull boolean isActive) {
}
