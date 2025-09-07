package carsharing.carsharingservice.mapper;

import carsharing.carsharingservice.config.MapperConfig;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public class DateMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Named("formatDate")
    public String asString(LocalDate date) {
        return date != null ? date.format(FORMATTER) : null;
    }

    @Named("formatDate")
    public LocalDate asLocalDate(String date) {
        return date != null ? LocalDate.parse(date, FORMATTER) : null;
    }
}