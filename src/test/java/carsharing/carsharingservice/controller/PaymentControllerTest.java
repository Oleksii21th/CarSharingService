package carsharing.carsharingservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import carsharing.carsharingservice.dto.payment.PaymentRequestDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseFullInfoDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentControllerTest extends AbstractControllerTest {

    static {
        removeAllSqlFilePaths = List.of(
                "database/payment/remove-all-payments.sql",
                "database/rental/remove-all-rentals.sql",
                "database/car/remove-all-cars.sql",
                "database/user/remove-all-users.sql"
        );

        insertDefaultSqlFilePaths = List.of(
                "database/user/add-default-users.sql",
                "database/car/add-default-cars.sql",
                "database/rental/add-default-rentals.sql",
                "database/payment/add-default-payments.sql"
        );
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void findAllPayments_UserHasPayments_ReturnsPaymentsList() throws Exception {
        setMockCustomerUser();

        MvcResult result = mockMvc.perform(get("/payments")
                        .param("user_id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<PaymentResponseDto> payments = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        assertThat(payments).isNotEmpty();
        assertThat(payments.get(0).getStatus()).isEqualTo("PENDING");
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void createPayment_ValidRequestDto_ReturnsCreatedPayment() throws Exception {
        setMockCustomerUser();

        PaymentRequestDto requestDto = new PaymentRequestDto(2L, "PAYMENT");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/payments")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PaymentResponseDto payment = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentResponseDto.class);

        assertThat(payment.getRentalId()).isEqualTo(2L);
        assertThat(payment.getType()).isEqualTo("PAYMENT");
        assertThat(payment.getAmountToPay()).isNotNull();
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void paymentSuccess_ValidSessionId_ReturnsFullInfo() throws Exception {
        MvcResult result = mockMvc.perform(get("/payments/success")
                        .param("session_id", "session1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PaymentResponseFullInfoDto payment = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentResponseFullInfoDto.class);

        assertThat(payment.getType()).isEqualTo("PAYMENT");
        assertThat(payment.getAmountToPay()).isNotNull();
        assertThat(payment.getRental()).isInstanceOf(RentalResponseDto.class);
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void paymentCancel_ValidSessionId_ReturnsPending() throws Exception {
        MvcResult result = mockMvc.perform(get("/payments/cancel")
                        .param("session_id", "session1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PaymentResponseFullInfoDto payment = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentResponseFullInfoDto.class);

        assertThat(payment.getType()).isEqualTo("PAYMENT");
        assertThat(payment.getAmountToPay()).isNotNull();
    }

    private void setMockCustomerUser() {
        User mockUser = new User();
        mockUser.setId(1L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser,
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
                )
        );
    }
}
