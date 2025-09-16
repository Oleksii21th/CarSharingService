package carsharing.carsharingservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import carsharing.carsharingservice.dto.car.CarRequestDto;
import carsharing.carsharingservice.dto.car.CarResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarControllerTest extends AbstractControllerTest {

    static {
        AbstractControllerTest.insertDefaultSqlFilePath = "database/car/add-default-cars.sql";
        AbstractControllerTest.removeAllSqlFilePath = "database/car/remove-all-cars.sql";
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void findAllCars_ReturnsList() throws Exception {
        MvcResult result = mockMvc.perform(get("/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<CarResponseDto> cars = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        assertThat(cars).hasSize(2);
        assertThat(cars.get(0).getModel()).isEqualTo("Test");
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void findCarById_ReturnsCar() throws Exception {
        MvcResult result = mockMvc.perform(get("/cars/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarResponseDto car = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarResponseDto.class);

        assertThat(car.getBrand()).isEqualTo("Test");
        assertThat(car.getDailyFee()).isEqualTo(BigDecimal.valueOf(2).setScale(2));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void createCar_ValidRequestDto_Success() throws Exception {
        CarRequestDto requestDto = new CarRequestDto();
        requestDto.setModel("NewTest");
        requestDto.setBrand("NewTest");
        requestDto.setType("SEDAN");
        requestDto.setInventory(5);
        requestDto.setDailyFee(BigDecimal.valueOf(10));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/cars")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarResponseDto.class);

        assertThat(actual.getModel()).isEqualTo("NewTest");
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void updateCar_ReturnsUpdatedCar() throws Exception {
        CarRequestDto requestDto = new CarRequestDto();
        requestDto.setModel("UpdatedTest");
        requestDto.setBrand("UpdatedTest2");
        requestDto.setType("SEDAN");
        requestDto.setInventory(10);
        requestDto.setDailyFee(BigDecimal.valueOf(20));

        String json = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(patch("/cars/2")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarResponseDto updatedCar = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarResponseDto.class);

        assertThat(updatedCar.getModel()).isEqualTo("UpdatedTest");
        assertThat(updatedCar.getBrand()).isEqualTo("UpdatedTest2");
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void deleteCarById_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/cars/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
