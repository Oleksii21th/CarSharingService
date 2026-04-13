package carsharing.carsharingservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import carsharing.carsharingservice.dto.rental.RentalDetailsDto;
import carsharing.carsharingservice.dto.rental.RentalRequestDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
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
import carsharing.carsharingservice.security.AccessManager;
import carsharing.carsharingservice.service.impl.RentalServiceImpl;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
    @Spy
    private AccessManager accessManager;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private User user;
    private Car car;
    private Rental rental;
    private RentalRequestDto rentalRequestDto;
    private RentalDetailsDto rentalDetailsDto;
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

        rentalDetailsDto = new RentalDetailsDto(
                1L,
                rentalRequestDto.getRentalDate(),
                rentalRequestDto.getReturnDate(),
                null,
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
    @DisplayName("Customer sees only own rentals")
    void findRentalsByUser_Customer_ReturnsOwnRentals() {
        RentalSearchParametersDto params =
                new RentalSearchParametersDto(1L, true);

        Authentication authentication = mock(Authentication.class);

        SimpleGrantedAuthority simpleGrantedAuthority =
                new SimpleGrantedAuthority("ROLE_CUSTOMER");
        Collection<SimpleGrantedAuthority> authCollection =
                Collections.singleton(simpleGrantedAuthority);

        when(authentication.getPrincipal()).thenReturn(user);
        when(authentication.getAuthorities())
                .thenReturn((Collection) authCollection);

        when(rentalRepository.findByUserIdAndIsActive(1L, true))
                .thenReturn(List.of(rental));

        when(rentalMapper.toDto(rental)).thenReturn(rentalResponseDto);

        List<RentalResponseDto> result =
                rentalService.findRentalsByUser(params, authentication);

        assertThat(result).containsExactly(rentalResponseDto);
        verify(rentalRepository).findByUserIdAndIsActive(1L, true);
    }

    @Test
    @DisplayName("Manager can fetch any user's rentals")
    void findRentalsByUser_Manager_ReturnsAnyUserRentals() {
        Authentication authentication = mock(Authentication.class);

        User manager = new User();
        manager.setId(5L);

        SimpleGrantedAuthority simpleGrantedAuthority =
                new SimpleGrantedAuthority("ROLE_MANAGER");
        Collection<SimpleGrantedAuthority> authCollection =
                Collections.singleton(simpleGrantedAuthority);

        when(authentication.getPrincipal()).thenReturn(manager);
        when(authentication.getAuthorities())
                .thenReturn((Collection) authCollection);

        when(rentalRepository.findByUserIdAndIsActive(5L, true))
                .thenReturn(List.of(rental));

        when(rentalMapper.toDto(rental)).thenReturn(rentalResponseDto);

        RentalSearchParametersDto params =
                new RentalSearchParametersDto(5L, true);

        List<RentalResponseDto> result =
                rentalService.findRentalsByUser(params, authentication);

        assertThat(result).containsExactly(rentalResponseDto);
    }

    @Test
    @DisplayName("Finds rental by id for owner")
    void findRentalById_Owner_ReturnsDto() {
        Authentication authentication = mock(Authentication.class);

        rental.setUser(user);

        SimpleGrantedAuthority simpleGrantedAuthority =
                new SimpleGrantedAuthority("ROLE_CUSTOMER");
        Collection<SimpleGrantedAuthority> authCollection =
                Collections.singleton(simpleGrantedAuthority);

        when(authentication.getPrincipal()).thenReturn(user);
        when(authentication.getAuthorities())
                .thenReturn((Collection) authCollection);

        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));
        when(rentalMapper.toDetailsDto(rental)).thenReturn(rentalDetailsDto);

        RentalDetailsDto result =
                rentalService.findRentalById(1L, authentication);

        assertThat(result).isEqualTo(rentalDetailsDto);
    }

    @Test
    @DisplayName("Throws AccessDeniedException when not owner")
    void findRentalById_NotOwner_ThrowsException() {
        Authentication authentication = mock(Authentication.class);

        User otherUser = new User();
        otherUser.setId(2L);

        rental.setUser(user);

        SimpleGrantedAuthority simpleGrantedAuthority =
                new SimpleGrantedAuthority("ROLE_CUSTOMER");
        Collection<SimpleGrantedAuthority> authCollection =
                Collections.singleton(simpleGrantedAuthority);

        when(authentication.getPrincipal()).thenReturn(otherUser);
        when(authentication.getAuthorities())
                .thenReturn((Collection) authCollection);

        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));

        assertThrows(AccessDeniedException.class,
                () -> rentalService.findRentalById(1L, authentication));
    }

    @Test
    @DisplayName("Throws RentalNotFoundException when id not found")
    void findRentalById_NotFound_ThrowsException() {
        Authentication authentication = mock(Authentication.class);

        when(rentalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RentalNotFoundException.class,
                () -> rentalService.findRentalById(99L, authentication));
    }

    @Test
    @DisplayName("Returns rental successfully")
    void returnRental_Valid_ReturnsDto() {
        when(rentalRepository.findByUserIdAndId(1L, 1L)).thenReturn(Optional.of(rental));
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(rentalResponseDto);

        RentalResponseDto result = rentalService.returnRental(1L, 1L);

        assertThat(result).isEqualTo(rentalResponseDto);
        assertThat(rental.isActive()).isFalse();
        verify(carRepository).save(car);
    }

    @Test
    @DisplayName("Throws TwiceReturnedRentalException if already returned")
    void returnRental_AlreadyReturned_ThrowsException() {
        rental.setActive(false);
        when(rentalRepository.findByUserIdAndId(1L, 1L))
                .thenReturn(Optional.of(rental));

        assertThrows(TwiceReturnedRentalException.class, () ->
                rentalService.returnRental(1L, 1L));
    }

    @Test
    @DisplayName("Throws RentalNotFoundException if rental not found")
    void returnRental_NotFound_ThrowsException() {
        when(rentalRepository.findByUserIdAndId(1L, 99L))
                .thenReturn(Optional.empty());

        assertThrows(RentalNotFoundException.class, () ->
                rentalService.returnRental(1L, 99L));
    }
}
