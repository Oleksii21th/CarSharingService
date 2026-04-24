package carsharing.carsharingservice.dto.user;

import carsharing.carsharingservice.model.Role;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequestDto(@NotNull Role role) {
}
