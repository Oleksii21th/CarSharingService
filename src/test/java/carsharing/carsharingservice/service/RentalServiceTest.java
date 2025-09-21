package carsharing.carsharingservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import carsharing.carsharingservice.dto.rental.RentalRequestDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.dto.rental.RentalReturnDateDto;
import carsharing.carsharingservice.dto.rental.RentalSearchParametersDto;
import carsharing.carsharingservice.exception.badrequest.ActivePaymentsException;
import carsharing.carsharingservice.exception.badrequest.EmptyCarInventoryException;
import carsharing.carsharingservice.exception.badrequest.InvalidRentalDateException;
import carsharing.carsharingservice.exception.badrequest.TwiceReturnedRentalException;
import carsharing.carsharingservice.exception.notfound.CarNotFoundException;
import carsharing.carsharingservice.exception.notfound.RentalNotFoundException;
import carsharing.carsharingservice.mapper.RentalMapper;
import carsharing.carsharingservice.model.Car;
import carsharing.carsharingservice.model.Payment;
import carsharing.carsharingservice.model.PaymentStatus;
import carsharing.carsharingservice.model.Rental;
import carsharing.carsharingservice.model.User;
import carsharing.carsharingservice.repository.CarRepository;
import carsharing.carsharingservice.repository.PaymentRepository;
import carsharing.carsharingservice.repository.RentalRepository;
import carsharing.carsharingservice.repository.UserRepository;
import carsharing.carsharingservice.service.impl.RentalServiceImpl;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private TelegramNotificationService telegramNotificationService;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private User user;
    private Car car;
    private Rental rental;
    private RentalRequestDto rentalRequestDto;
    private RentalResponseDto rentalResponseDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        car = new Car();
        car.setId(1L);
        car.setInventory(1);

        rentalRequestDto = new RentalRequestDto(
                LocalDate.now().plusDays(1).format(FORMATTER),
                LocalDate.now().plusDays(3).format(FORMATTER),
                1L);

        rentalResponseDto = new RentalResponseDto(
                1L,
                rentalRequestDto.getRentalDate(),
                rentalRequestDto.getReturnDate(),
                null,
                null
        );

        rental = new Rental();
        rental.setId(1L);
        rental.setCar(car);
        rental.setUser(user);
        rental.setActive(true);
    }

    @Test
    @DisplayName("Saves rental successfully")
    void save_ValidRequest_ReturnsDto() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(paymentRepository.findByUserIdAndStatus(
                1L,
                PaymentStatus.PENDING)).thenReturn(List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(rentalMapper.toModel(rentalRequestDto)).thenReturn(rental);
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(rentalResponseDto);

        RentalResponseDto result = rentalService.save(1L, rentalRequestDto);

        assertThat(result).isEqualTo(rentalResponseDto);
        verify(carRepository).save(car);
        verify(telegramNotificationService).sendRentalCreatedNotification(rental);
    }

    @Test
    @DisplayName("Throws CarNotFoundException when car does not exist")
    void save_CarNotFound_ThrowsException() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());
        RentalRequestDto dto = new RentalRequestDto(
                LocalDate.now().plusDays(1).format(FORMATTER),
                LocalDate.now().plusDays(3).format(FORMATTER),
                99L);

        assertThrows(CarNotFoundException.class, () -> rentalService.save(1L, dto));
    }

    @Test
    @DisplayName("Throws EmptyCarInventoryException when no cars available")
    void save_EmptyInventory_ThrowsException() {
        car.setInventory(0);
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        assertThrows(EmptyCarInventoryException.class, () ->
                rentalService.save(1L, rentalRequestDto));
    }

    @Test
    @DisplayName("Throws InvalidRentalDateException when date is in the past")
    void save_PastDate_ThrowsException() {
        RentalRequestDto dto = new RentalRequestDto(
                LocalDate.now().minusDays(1).format(FORMATTER),
                LocalDate.now().plusDays(3).format(FORMATTER),
                1L
        );
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        assertThrows(InvalidRentalDateException.class, () -> rentalService.save(1L, dto));
    }

    @Test
    @DisplayName("Throws ActivePaymentsException when user has pending payments")
    void save_ActivePayments_ThrowsException() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        Payment payment = new Payment();
        payment.setRental(rental);
        when(paymentRepository.findByUserIdAndStatus(
                1L,
                PaymentStatus.PENDING)).thenReturn(List.of(payment));

        assertThrows(ActivePaymentsException.class, () ->
                rentalService.save(1L, rentalRequestDto));
    }

    @Test
    @DisplayName("Finds rentals by user")
    void findRentalsByUser_ReturnsList() {
        RentalSearchParametersDto params = new RentalSearchParametersDto(1L, true);
        when(rentalRepository.findByUserIdAndIsActive(1L, true)).thenReturn(List.of(rental));
        when(rentalMapper.toDto(rental)).thenReturn(rentalResponseDto);

        List<RentalResponseDto> result = rentalService.findRentalsByUser(params);

        assertThat(result).containsExactly(rentalResponseDto);
    }

    @Test
    @DisplayName("Finds rental by id")
    void findRentalById_ExistingId_ReturnsDto() {
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));
        when(rentalMapper.toDto(rental)).thenReturn(rentalResponseDto);

        RentalResponseDto result = rentalService.findRentalById(1L);

        assertThat(result).isEqualTo(rentalResponseDto);
    }

    @Test
    @DisplayName("Throws RentalNotFoundException when id not found")
    void findRentalById_NotFound_ThrowsException() {
        when(rentalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RentalNotFoundException.class, () -> rentalService.findRentalById(99L));
    }

    @Test
    @DisplayName("Returns rental successfully")
    void returnRental_Valid_ReturnsDto() {
        RentalReturnDateDto returnDto = new RentalReturnDateDto(1L);
        when(rentalRepository.findByUserIdAndId(1L, 1L)).thenReturn(Optional.of(rental));
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(rentalResponseDto);

        RentalResponseDto result = rentalService.returnRental(1L, returnDto);

        assertThat(result).isEqualTo(rentalResponseDto);
        assertThat(rental.isActive()).isFalse();
        verify(carRepository).save(car);
    }

    @Test
    @DisplayName("Throws TwiceReturnedRentalException if already returned")
    void returnRental_AlreadyReturned_ThrowsException() {
        rental.setActive(false);
        RentalReturnDateDto returnDto = new RentalReturnDateDto(1L);
        when(rentalRepository.findByUserIdAndId(1L, 1L))
                .thenReturn(Optional.of(rental));

        assertThrows(TwiceReturnedRentalException.class, () ->
                rentalService.returnRental(1L, returnDto));
    }

    @Test
    @DisplayName("Throws RentalNotFoundException if rental not found")
    void returnRental_NotFound_ThrowsException() {
        RentalReturnDateDto returnDto = new RentalReturnDateDto(99L);
        when(rentalRepository.findByUserIdAndId(1L, 99L))
                .thenReturn(Optional.empty());

        assertThrows(RentalNotFoundException.class, () ->
                rentalService.returnRental(1L, returnDto));
    }
}
