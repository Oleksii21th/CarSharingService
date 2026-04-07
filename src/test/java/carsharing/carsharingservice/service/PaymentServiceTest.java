package carsharing.carsharingservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import carsharing.carsharingservice.dto.payment.PaymentRequestDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseFullInfoDto;
import carsharing.carsharingservice.exception.notfound.PaymentNotFoundException;
import carsharing.carsharingservice.mapper.PaymentMapper;
import carsharing.carsharingservice.model.Car;
import carsharing.carsharingservice.model.Payment;
import carsharing.carsharingservice.model.PaymentStatus;
import carsharing.carsharingservice.model.PaymentType;
import carsharing.carsharingservice.model.Rental;
import carsharing.carsharingservice.model.User;
import carsharing.carsharingservice.repository.PaymentRepository;
import carsharing.carsharingservice.repository.RentalRepository;
import carsharing.carsharingservice.security.AccessManager;
import carsharing.carsharingservice.service.impl.PaymentServiceImpl;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private AccessManager accessManager;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("Returns list of user payments when they exist")
    void findAllPayments_UserHasPayments_ReturnsList() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setType(PaymentType.PAYMENT);
        payment.setStatus(PaymentStatus.PENDING);

        PaymentResponseDto dto = new PaymentResponseDto(
                1L, 1L, "PENDING", "PAYMENT", "session123",
                BigDecimal.valueOf(50), "test"
        );

        Authentication mockAuth = Mockito.mock(Authentication.class);
        when(accessManager.resolveUserId(mockAuth, 1L)).thenReturn(1L);

        when(paymentRepository.findPaymentsByUserId(1L)).thenReturn(List.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(dto);

        List<PaymentResponseDto> result = paymentService.findAllPayments(1L, mockAuth);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("PENDING");
        assertThat(result.get(0).getType()).isEqualTo("PAYMENT");
    }

    @Test
    @DisplayName("Creates new payment session and returns DTO")
    void savePaymentSession_NewPayment_CreatesAndReturnsDto() {
        Car car = new Car();
        car.setDailyFee(BigDecimal.valueOf(1));

        Rental rental = new Rental();
        rental.setId(1L);
        rental.setCar(car);
        rental.setRentalDate(LocalDate.now().minusDays(3));
        rental.setReturnDate(LocalDate.now().plusDays(2));

        User mockUser = new User();
        mockUser.setId(1L);
        rental.setUser(mockUser);

        PaymentRequestDto requestDto = new PaymentRequestDto(1L, "PAYMENT");

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setRental(rental);
        payment.setType(PaymentType.PAYMENT);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmountToPay(BigDecimal.valueOf(1));

        PaymentResponseDto responseDto = new PaymentResponseDto(
                1L, 1L, "PENDING", "PAYMENT", "session123",
                BigDecimal.valueOf(1), "Go to this link to finish payment: test"
        );

        Authentication mockAuth = Mockito.mock(Authentication.class);
        when(accessManager.resolveUserId(mockAuth, 1L)).thenReturn(1L);

        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));
        when(paymentRepository.findByRentalIdAndType(1L, PaymentType.PAYMENT))
                .thenReturn(List.of());
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toDto(any(Payment.class))).thenReturn(responseDto);

        Session mockSession = mock(Session.class);
        when(mockSession.getId()).thenReturn("session123");
        when(mockSession.getUrl()).thenReturn("test");

        try (MockedStatic<Session> mockedStatic = Mockito.mockStatic(Session.class)) {
            mockedStatic.when(() -> Session.create(Mockito.<SessionCreateParams>any()))
                    .thenReturn(mockSession);

            PaymentResponseDto result = paymentService.savePaymentSession(requestDto, mockAuth);

            assertThat(result.getRentalId()).isEqualTo(1L);
            assertThat(result.getType()).isEqualTo("PAYMENT");
            assertThat(result.getDescription()).contains("Go to this link to finish payment");
            verify(paymentRepository).save(any(Payment.class));
        }
    }

    @Test
    @DisplayName("Updates payment status and returns full DTO when session exists")
    void updatePaymentStatus_ValidSessionId_UpdatesAndReturnsDto() {
        Rental rental = new Rental();
        rental.setId(1L);

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setRental(rental);
        payment.setType(PaymentType.FINE);
        payment.setStatus(PaymentStatus.PENDING);

        PaymentResponseFullInfoDto fullDto = new PaymentResponseFullInfoDto(
                1L, 1L, "FINE", BigDecimal.valueOf(1), null
        );

        when(paymentRepository.findBySessionId("session1")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toFullInfoDto(payment)).thenReturn(fullDto);

        Session mockSession = mock(Session.class);
        when(mockSession.getPaymentStatus()).thenReturn("paid");

        try (MockedStatic<Session> mockedStatic = Mockito.mockStatic(Session.class)) {
            mockedStatic.when(() -> Session.retrieve("session1")).thenReturn(mockSession);

            PaymentResponseFullInfoDto result = paymentService.updatePaymentStatus("session1");

            assertThat(result.getType()).isEqualTo("FINE");
            verify(paymentRepository).save(payment);
        }
    }

    @Test
    @DisplayName("Throws exception when no payment is found by sessionId")
    void updatePaymentStatus_InvalidSessionId_ThrowsException() {
        when(paymentRepository.findBySessionId("noId")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.updatePaymentStatus("noId"))
                .isInstanceOf(PaymentNotFoundException.class);
    }
}
