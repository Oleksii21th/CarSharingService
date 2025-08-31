package carsharing.carsharingservice.mapper;

import carsharing.carsharingservice.config.MapperConfig;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public class DateMapper {
    @Named("formatDate")
    public String asString(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null;
    }
}