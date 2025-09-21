package carsharing.carsharingservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import carsharing.carsharingservice.dto.rental.RentalRequestDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.dto.rental.RentalReturnDateDto;
import carsharing.carsharingservice.service.TelegramNotificationService;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RentalControllerTest extends AbstractControllerTest {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private TelegramNotificationService telegramNotificationService;

    static {
        removeAllSqlFilePaths = List.of(
                "database/rental/remove-all-rentals.sql",
                "database/car/remove-all-cars.sql",
                "database/user/remove-all-users.sql"
        );

        insertDefaultSqlFilePaths = List.of(
                "database/car/add-default-cars.sql",
                "database/user/add-default-users.sql",
                "database/rental/add-default-rentals.sql"
        );
    }

    @TestConfiguration
    static class TelegramTestConfig {
        @Bean
        public TelegramNotificationService telegramNotificationService() {
            return Mockito.mock(TelegramNotificationService.class);
        }
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void findRentalById_ValidId_ReturnsRental() throws Exception {
        MvcResult result = mockMvc.perform(get("/rentals/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalResponseDto rental = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                RentalResponseDto.class
        );

        assertThat(rental.getId()).isEqualTo(2L);
        assertThat(rental.getCar().getModel()).isEqualTo("Test");
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void findRentalsByUser_ValidParams_ReturnsList() throws Exception {
        MvcResult result = mockMvc.perform(get("/rentals")
                        .param("userId", "1")
                        .param("isActive", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<RentalResponseDto> rentals = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        assertThat(rentals).hasSize(1);
        assertThat(rentals.get(0).getCar().getBrand()).isEqualTo("Test");
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void createRental_ValidRequestDto_ReturnsCreatedRental() throws Exception {
        Mockito.doNothing().when(telegramNotificationService)
                .sendRentalCreatedNotification(Mockito.any());

        String rentalDate = LocalDate.now().plusDays(1).format(FORMATTER);
        String returnDate = LocalDate.now().plusDays(3).format(FORMATTER);
        RentalRequestDto dto = new RentalRequestDto(rentalDate, returnDate, 2L);

        String jsonRequest = objectMapper.writeValueAsString(dto);

        MvcResult result = mockMvc.perform(post("/rentals")
                        .param("userId", "1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), RentalResponseDto.class);

        assertThat(actual.getCar().getId()).isEqualTo(2L);
        assertThat(actual.getRentalDate()).isEqualTo(rentalDate);
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void returnRental_ValidId_ReturnsUpdatedRental() throws Exception {
        RentalReturnDateDto returnDateDto = new RentalReturnDateDto(2L);

        String json = objectMapper.writeValueAsString(returnDateDto);

        MvcResult result = mockMvc.perform(post("/rentals/1/return")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalResponseDto returned = objectMapper.readValue(
                result.getResponse().getContentAsString(), RentalResponseDto.class
        );

        assertThat(returned.getActualReturnDate()).isNotNull();
        assertThat(returned.getId()).isEqualTo(2L);
    }
}
